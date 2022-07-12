package com.graph.graph.graphview;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import com.graph.graph.utils.UtilitiesPoint2D;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.graph.graph.utils.UtilitiesJavaFX.pick;
import static com.graph.graph.utils.UtilitiesPoint2D.attractiveForce;
import static com.graph.graph.utils.UtilitiesPoint2D.repellingForce;

public class GraphPanel extends Pane {

    /*
    CONFIGURATION PROPERTIES
     */
    private final GraphProperties graphProperties;

    /*
    INTERNAL DATA STRUCTURE
     */
    private final Graph theGraph;
    private final Map<Vertex, GraphVertexNode> vertexNodes;
    private final Map<Edge, GraphEdgeBase> edgeNodes;
    private Map<Edge, Tuple<Vertex>> connections;
    private final Map<Tuple<GraphVertexNode>, Integer> placedEdges = new HashMap<>();
    private boolean initialized = false;
    private final boolean edgesWithArrows;

    private State state;

    /*
    INTERACTION WITH VERTICES AND EDGES
     */
    private Consumer<GraphVertex> vertexDoubleClickConsumer = null;
    private Consumer<GraphVertex> vertexPickConsumer = null;
    private Consumer<GraphEdge> edgeDoubleClickConsumer = null;

    /*
    AUTOMATIC LAYOUT RELATED ATTRIBUTES
     */
    public final BooleanProperty automaticLayoutProperty;
    private AnimationTimer timer;
    private final double repulsionForce;
    private final double attractionForce;
    private final double attractionScale;

    //This value was obtained experimentally
    private static final int AUTOMATIC_LAYOUT_ITERATIONS = 20;

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using default properties and default random
     * placement of vertices.
     *
     * @param theGraph underlying graph
     * @see Graph
     */
    public GraphPanel(Graph theGraph) {
        this(theGraph, new GraphProperties(), null, null);
    }


    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and custom placement of
     * vertices.
     *
     * @param theGraph   underlying graph
     * @param properties custom properties, null for default
     */
    public GraphPanel(Graph theGraph, GraphProperties properties) {

        this(theGraph, properties, null, null);
    }

    /**
     * Constructs a visualization of the graph referenced by
     * <code>theGraph</code>, using custom properties and custom placement of
     * vertices.
     *
     * @param theGraph   underlying graph
     * @param properties custom properties, null for default
     * @param cssFile    alternative css file, instead of default 'graph.css'
     */
    public GraphPanel(Graph theGraph, GraphProperties properties, URI cssFile, State state) {

        if (theGraph == null) {
            throw new IllegalArgumentException("The graph cannot be null.");
        }
        this.theGraph = theGraph;
        this.graphProperties = properties != null ? properties : new GraphProperties();

        this.edgesWithArrows = this.graphProperties.getUseEdgeArrow();

        this.repulsionForce = this.graphProperties.getRepulsionForce();
        this.attractionForce = this.graphProperties.getAttractionForce();
        this.attractionScale = this.graphProperties.getAttractionScale();

        vertexNodes = new HashMap<>();
        edgeNodes = new HashMap<>();
        connections = new HashMap<>();

        this.state = state == null ? createDefaultState() : state;
        //set stylesheet and class
        loadStylesheet(cssFile);

        initNodes();
        updateLabels();
        enableMouseListener();
        //automatic layout initializations
        timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                runLayoutIteration();
            }
        };

        this.automaticLayoutProperty = new SimpleBooleanProperty(false);
        this.automaticLayoutProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                timer.start();
            } else {
                timer.stop();
            }
        });

    }

    private State createDefaultState() {
        List<Vertex> vertices = new ArrayList<>(theGraph.getVertices());
        List<Edge> edges = new ArrayList<>(theGraph.getEdges());
        return new State(vertices, edges);
    }

    private synchronized void runLayoutIteration() {
        for (int i = 0; i < AUTOMATIC_LAYOUT_ITERATIONS; i++) {
            resetForces();
            computeForces();
            updateForces();
        }
        applyForces();
    }

    /**
     * Runs the initial current vertex placement strategy.
     * <p>
     * This method should only be called once during the lifetime of the object
     * and only after the underlying @link Scene is displayed.
     * <p>
     * Further required updates should be performed through the {@link #update()
     * } method.
     *
     * @throws IllegalStateException The exception is thrown if: (1) the Scene
     *                               is not yet displayed; (2) It has zero width and/or height, and; (3) If
     *                               this method was already called.
     */
    public void init() throws IllegalStateException {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        } else if (this.getWidth() == 0 || this.getHeight() == 0) {
            throw new IllegalStateException("The layout for this panel has zero width and/or height");
        } else if (this.initialized) {
            throw new IllegalStateException("Already initialized. Use update() method instead.");
        }

        this.initialized = true;
    }

    /**
     * Returns the property used to toggle the automatic layout of vertices.
     *
     * @return automatic layout property
     */
    public BooleanProperty automaticLayoutProperty() {
        return this.automaticLayoutProperty;
    }

    /**
     * Toggle the automatic layout of vertices.
     *
     * @param value true if enabling; false, otherwise
     */
    public void setAutomaticLayout(boolean value) {
        automaticLayoutProperty.set(value);
    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph, immediately returning to the caller.
     * <p>
     * This method invokes the refresh in the graphical
     * thread through Platform.runLater(), so its not guaranteed that the visualization is in sync
     * immediately after this method finishes. That is, this method
     * immediately returns to the caller without waiting for the update to the
     * visualization.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     */
    public void update() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
            throw new IllegalStateException("You must call init() method before any updates.");
        }

        //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
        Platform.runLater(() -> {
            updateNodes();
        });

    }

    /**
     * Forces a refresh of the visualization based on current state of the
     * underlying graph and waits for completion of the update.
     * <p>
     * Use this variant only when necessary, e.g., need to style an element
     * immediately after adding it to the underlying graph. Otherwise, use
     * {@link #update() } instead for performance sake.
     * <p>
     * New vertices will be added close to adjacent ones or randomly for
     * isolated vertices.
     */
    public void updateAndWait() {
        if (this.getScene() == null) {
            throw new IllegalStateException("You must call this method after the instance was added to a scene.");
        }

        if (!this.initialized) {
            throw new IllegalStateException("You must call init() method before any updates.");
        }

        final FutureTask update = new FutureTask(new Callable() {
            @Override
            public Boolean call() throws Exception {
                updateNodes();
                return true;
            }
        });

        //
        if (!Platform.isFxApplicationThread()) {
            //this will be called from a non-javafx thread, so this must be guaranteed to run of the graphics thread
            Platform.runLater(update);

            //wait for completion, only outside javafx thread; otherwise -> deadlock
            try {
                update.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            updateNodes();
        }

    }

    private synchronized void updateNodes() {
        removeNodes();
        insertNodes();
        updateLabels();
    }

    /*
    INTERACTION WITH VERTICES AND EDGES
     */

    /**
     * Sets the action that should be performed when a vertex is double clicked.
     *
     * @param action action to be performed
     */
    public void setVertexDoubleClickAction(Consumer<GraphVertex> action) {
        this.vertexDoubleClickConsumer = action;
    }

    public void setVertexAltClickAction(Consumer<GraphVertex> action) {
        this.vertexPickConsumer = action;
    }

    /**
     * Sets the action that should be performed when an edge is double clicked.
     *
     * @param action action to be performed
     */
    public void setEdgeDoubleClickAction(Consumer<GraphEdge> action) {
        this.edgeDoubleClickConsumer = action;
    }

    /*
    NODES CREATION/UPDATES
     */
    private void initNodes() {

        /* create vertex graphical representations */
        for (Vertex vertex : listOfVertices()) {
            GraphVertexNode vertexAnchor = new GraphVertexNode(vertex, graphProperties.getVertexRadius(), graphProperties.getVertexAllowUserMove());

            vertexNodes.put(vertex, vertexAnchor);
        }

        /* create edges graphical representations between existing vertices */
        //this is used to guarantee that no duplicate edges are ever inserted
        List<Edge> edgesToPlace = listOfEdges();

        for (Vertex vertex : vertexNodes.keySet()) {

            Iterable<Edge> incidentEdges = theGraph.incidentEdges(vertex);

            for (Edge edge : incidentEdges) {

                //if already plotted, ignore edge.
                if (!edgesToPlace.contains(edge)) {
                    continue;
                }

                Vertex oppositeVertex = theGraph.opposite(vertex, edge);

                GraphVertexNode graphVertexIn = vertexNodes.get(vertex);
                GraphVertexNode graphVertexOppositeOut = vertexNodes.get(oppositeVertex);

                graphVertexIn.addAdjacentVertex(graphVertexOppositeOut);
                graphVertexOppositeOut.addAdjacentVertex(graphVertexIn);

                GraphEdgeBase graphEdge = createEdge(edge, graphVertexIn, graphVertexOppositeOut);

                /* Track Edges already placed */
                connections.put(edge, new Tuple<>(vertex, oppositeVertex));
                addEdge(graphEdge, edge);

                if (this.edgesWithArrows) {
                    Arrow arrow = new Arrow(this.graphProperties.getEdgeArrowSize());
                    graphEdge.attachArrow(arrow);
                    this.getChildren().add(arrow);
                }

                edgesToPlace.remove(edge);
            }

        }

        /* place vertices above lines */
        for (Vertex vertex : vertexNodes.keySet()) {
            GraphVertexNode v = vertexNodes.get(vertex);

            addVertex(v);
        }
    }

    private GraphEdgeBase createEdge(Edge edge, GraphVertexNode graphVertexInbound, GraphVertexNode graphVertexOutbound) {
        /*
        Even if edges are later removed, the corresponding index remains the same. Otherwise, we would have to
        regenerate the appropriate edges.
         */
        int edgeIndex = 0;
        Integer counter = placedEdges.get(new Tuple(graphVertexInbound, graphVertexOutbound));
        if (counter != null) {
            edgeIndex = counter;
        }

        GraphEdgeBase graphEdge;

        graphEdge = new GraphEdgeLine(edge, graphVertexInbound, graphVertexOutbound);


        placedEdges.put(new Tuple(graphVertexInbound, graphVertexOutbound), ++edgeIndex);

        return graphEdge;
    }

    private void addVertex(GraphVertexNode v) {
        this.getChildren().add(v);

        String labelText = v.getId();

        if (graphProperties.getUseVertexTooltip()) {
            Tooltip t = new Tooltip(labelText);
            Tooltip.install(v, t);
        }

        if (graphProperties.getUseVertexLabel()) {
            Label label = new Label(labelText);

            label.addStyleClass("vertex-id");
            this.getChildren().add(label);
            v.attachLabel(label);
        }
        Label label = new Label("∞");
        label.addStyleClass("vertex-distance");
        this.getChildren().add(label);
        v.attachDistanceLabel(label);
    }

    private void addEdge(GraphEdgeBase e, Edge edge) {
        //edges to the back
        this.getChildren().add(0, (Node) e);
        edgeNodes.put(edge, e);

        String labelText = edge.getWeight() + "";

        if (graphProperties.getUseEdgeTooltip()) {
            Tooltip t = new Tooltip(labelText);
            Tooltip.install((Node) e, t);
        }

        if (graphProperties.getUseEdgeLabel()) {
            Label label = new Label(labelText);

            label.addStyleClass("edge-label");
            this.getChildren().add(label);
            e.attachLabel(label);
        }
    }

    private void insertNodes() {
        Collection<Vertex> unplottedVertices = unplottedVertices();

        List<GraphVertexNode> newVertices = null;

        Bounds bounds = getPlotBounds();
        double mx = bounds.getMinX() + bounds.getWidth() / 2.0;
        double my = bounds.getMinY() + bounds.getHeight() / 2.0;

        if (!unplottedVertices.isEmpty()) {

            newVertices = new LinkedList<>();

            for (Vertex vertex : unplottedVertices) {
                //create node
                //Place new nodes in the vicinity of existing adjacent ones;
                //Place them in the middle of the plot, otherwise.
                double x, y;
                Collection<Edge> incidentEdges = theGraph.incidentEdges(vertex);
                if (incidentEdges.isEmpty()) {
                    /* not (yet) connected, put in the middle of the plot */
                    x = mx;
                    y = my;
                } else {
                    Edge firstEdge = incidentEdges.iterator().next();
                    Vertex opposite = theGraph.opposite(vertex, firstEdge);
                    GraphVertexNode existing = vertexNodes.get(opposite);

                    if (existing == null) {
                        /*
                        Updates may be coming too fast and we can get out of sync.
                        The opposite vertex exists in the (di)graph, but we have not yet
                        created it for the panel. Therefore, its position is unknown,
                        so place the vertex representation in the middle.
                        */
                        x = mx;
                        y = my;
                    } else {
                        /* TODO: fix -- the placing point can be set out of bounds*/
                        Point2D p = UtilitiesPoint2D.rotate(existing.getPosition().add(50.0, 50.0), existing.getPosition(), Math.random() * 360);

                        x = p.getX();
                        y = p.getY();
                    }
                }
//                vertex.setPosition(x, y);
                GraphVertexNode newVertex = new GraphVertexNode(vertex, graphProperties.getVertexRadius(), graphProperties.getVertexAllowUserMove());

                //track new nodes
                newVertices.add(newVertex);
                //add to global mapping
                vertexNodes.put(vertex, newVertex);
            }

        }

        Collection<Edge> unplottedEdges = unplottedEdges();
        if (!unplottedEdges.isEmpty()) {
            for (Edge edge : unplottedEdges) {

                Vertex[] vertices = edge.vertices();
                Vertex u = vertices[0]; //oubound if digraph, by javadoc requirement
                Vertex v = vertices[1]; //inbound if digraph, by javadoc requirement

                GraphVertexNode graphVertexOut = vertexNodes.get(u);
                GraphVertexNode graphVertexIn = vertexNodes.get(v);

                /*
                Updates may be coming too fast and we can get out of sync.
                Skip and wait for another update call, since they will surely
                be coming at this pace.
                */
                if (graphVertexIn == null || graphVertexOut == null) {
                    continue;
                }

                graphVertexOut.addAdjacentVertex(graphVertexIn);
                graphVertexIn.addAdjacentVertex(graphVertexOut);

                GraphEdgeBase graphEdge = createEdge(edge, graphVertexIn, graphVertexOut);

                if (this.edgesWithArrows) {
                    Arrow arrow = new Arrow(this.graphProperties.getEdgeArrowSize());
                    graphEdge.attachArrow(arrow);
                    this.getChildren().add(arrow);
                }

                /* Track edges */
                connections.put(edge, new Tuple<>(u, v));
                addEdge(graphEdge, edge);

            }
        }

        if (newVertices != null) {
            for (GraphVertexNode v : newVertices) {
                addVertex(v);
            }
        }

    }

    private void removeNodes() {
        //remove edges (graphical elements) that were removed from the underlying graph
        Collection<Edge> removedEdges = removedEdges();
        for (Edge e : removedEdges) {
            GraphEdgeBase edgeToRemove = edgeNodes.get(e);
            edgeNodes.remove(e);
            removeEdge(edgeToRemove);   //remove from panel

            //when edges are removed, the adjacency between vertices changes
            //the adjacency is kept in parallel in an internal data structure
            Tuple<Vertex> vertexTuple = connections.get(e);

            if (getTotalEdgesBetween(vertexTuple.first, vertexTuple.second) == 0) {
                GraphVertexNode v0 = vertexNodes.get(vertexTuple.first);
                GraphVertexNode v1 = vertexNodes.get(vertexTuple.second);

                v0.removeAdjacentVertex(v1);
                v1.removeAdjacentVertex(v0);
            }

            connections.remove(e);
        }

        //remove vertices (graphical elements) that were removed from the underlying graph
        Collection<Vertex> removedVertices = removedVertices();
        for (Vertex removedVertex : removedVertices) {
            GraphVertexNode removed = vertexNodes.remove(removedVertex);
            removeVertex(removed);
        }

    }

    private void removeEdge(GraphEdgeBase e) {
        getChildren().remove((Node) e);

        Arrow attachedArrow = e.getAttachedArrow();
        if (attachedArrow != null) {
            getChildren().remove(attachedArrow);
        }

        Text attachedLabel = e.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
    }

    private void removeVertex(GraphVertexNode v) {
        getChildren().remove(v);

        Text attachedLabel = v.getAttachedLabel();
        if (attachedLabel != null) {
            getChildren().remove(attachedLabel);
        }
    }

    /**
     * Updates node's labels
     */
    private void updateLabels() {
        theGraph.getVertices().forEach((v) -> {
            GraphVertexNode vertexNode = vertexNodes.get(v);
            if (vertexNode != null) {
                Label label = vertexNode.getAttachedLabel();
                if (label != null) {
                    String text = v.getId();
                    label.setText(text);
                }
            }
        });

        theGraph.getEdges().forEach((e) -> {
            GraphEdgeBase edgeNode = edgeNodes.get(e);
            if (edgeNode != null) {
                Label label = edgeNode.getAttachedLabel();
                if (label != null) {
                    String text;
                    if (e.getWeight() == Double.MAX_VALUE) {
                        text = "∞";
                    } else {
                        text = String.valueOf(e.getWeight());
                    }
                    label.setText(text);
                }
            }
        });
    }

    private Bounds getPlotBounds() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        if (vertexNodes.size() == 0)
            return new BoundingBox(0, 0, getWidth(), getHeight());

        for (GraphVertexNode v : vertexNodes.values()) {
            minX = Math.min(minX, v.getCenterX());
            minY = Math.min(minY, v.getCenterY());
            maxX = Math.max(maxX, v.getCenterX());
            maxY = Math.max(maxY, v.getCenterY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }


    /*
     * AUTOMATIC LAYOUT
     */
    private void computeForces() {
        for (GraphVertexNode v : vertexNodes.values()) {
            for (GraphVertexNode other : vertexNodes.values()) {
                if (v == other) {
                    continue; //NOP
                }

                //double k = Math.sqrt(getWidth() * getHeight() / graphVertexMap.size());
                Point2D repellingForce = repellingForce(v.getUpdatedPosition(), other.getUpdatedPosition(), this.repulsionForce);

                double deltaForceX = 0, deltaForceY = 0;

                //compute attractive and reppeling forces
                //opt to use internal areAdjacent check, because a vertex can be removed from
                //the underlying graph before we have the chance to remove it from our
                //internal data structure
                if (areAdjacent(v, other)) {

                    Point2D attractiveForce = attractiveForce(v.getUpdatedPosition(), other.getUpdatedPosition(), vertexNodes.size(), this.attractionForce, this.attractionScale);

                    deltaForceX = attractiveForce.getX() + repellingForce.getX();
                    deltaForceY = attractiveForce.getY() + repellingForce.getY();
                } else {
                    deltaForceX = repellingForce.getX();
                    deltaForceY = repellingForce.getY();
                }

                v.addForceVector(deltaForceX, deltaForceY);
            }
        }
    }

    private boolean areAdjacent(GraphVertexNode v, GraphVertexNode u) {
        return v.isAdjacentTo(u);
    }

    private void updateForces() {
        vertexNodes.values().forEach((v) -> {
            v.updateDelta();
        });
    }

    private void applyForces() {
        vertexNodes.values().forEach((v) -> {
            v.moveFromForces();
        });
    }

    private void resetForces() {
        vertexNodes.values().forEach((v) -> {
            v.resetForces();
        });
    }

    private int getTotalEdgesBetween(Vertex v, Vertex u) {
        //TODO: It may be necessary to adjust this method if you use another Graph
        //variant, e.g., Digraph (directed graph)
        int count = 0;
        for (Edge edge : theGraph.getEdges()) {
            if (edge.vertices()[0] == v && edge.vertices()[1] == u || edge.vertices()[0] == u && edge.vertices()[1] == v) {
                count++;
            }
        }
        return count;
    }

    private List<Edge> listOfEdges() {
        List<Edge> list = new LinkedList<>();
        for (Edge edge : theGraph.getEdges()) {
            list.add(edge);
        }
        return list;
    }

    private List<Vertex> listOfVertices() {
        List<Vertex> list = new LinkedList<>();
        for (Vertex vertex : theGraph.getVertices()) {
            list.add(vertex);
        }
        return list;
    }

    /**
     * Computes the vertex collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of vertices
     */
    private Collection<Vertex> unplottedVertices() {
        List<Vertex> unplotted = new LinkedList<>();

        for (Vertex v : theGraph.getVertices()) {
            if (!vertexNodes.containsKey(v)) {
                unplotted.add(v);
            }
        }

        return unplotted;
    }

    /**
     * Computes the collection for vertices that are currently being displayed but do
     * not longer exist in the underlying graph.
     *
     * @return collection of vertices
     */
    private Collection<Vertex> removedVertices() {
        List<Vertex> removed = new LinkedList<>();

        Collection<Vertex> graphVertices = theGraph.getVertices();
        Collection<GraphVertexNode> plotted = vertexNodes.values();

        for (GraphVertexNode v : plotted) {
            if (!graphVertices.contains(v.getUnderlyingVertex())) {
                removed.add(v.getUnderlyingVertex());
            }
        }

        return removed;
    }

    /**
     * Computes the collection for edges that are currently being displayed but do
     * not longer exist in the underlying graph.
     *
     * @return collection of edges
     */
    private Collection<Edge> removedEdges() {
        List<Edge> removed = new LinkedList<>();

        Collection<Edge> graphEdges = theGraph.getEdges();
        Collection<GraphEdgeBase> plotted = edgeNodes.values();

        for (GraphEdgeBase e : plotted) {
            if (!graphEdges.contains(e.getUnderlyingEdge())) {
                removed.add(e.getUnderlyingEdge());
            }
        }

        return removed;
    }

    /**
     * Computes the edge collection of the underlying graph that are not
     * currently being displayed.
     *
     * @return collection of edges
     */
    private Collection<Edge> unplottedEdges() {
        List<Edge> unplotted = new LinkedList<>();

        for (Edge e : theGraph.getEdges()) {
            if (!edgeNodes.containsKey(e)) {
                unplotted.add(e);
            }
        }

        return unplotted;
    }

    /**
     * Sets a vertex position (its center) manually.
     * <p>
     * The positioning should be inside the boundaries of the panel, but
     * no restrictions are enforced by this method, so be aware.
     *
     * @param v underlying vertex
     * @param x x-coordinate on panel
     * @param y y-coordinate on panel
     */
    public void setVertexPosition(Vertex v, double x, double y) {
        GraphVertexNode node = vertexNodes.get(v);
        if (node != null) {
            node.setPosition(x, y);
        }
    }

    /**
     * Return the current x-coordinate (relative to the panel) of a vertex.
     *
     * @param v underlying vertex
     * @return the x-coordinate or NaN if the vertex does not exist
     */
    public double getVertexPositionX(Vertex v) {
        GraphVertexNode node = vertexNodes.get(v);
        if (node != null) {
            return node.getPositionCenterX();
        }
        return Double.NaN;
    }

    /**
     * Return the current y-coordinate (relative to the panel) of a vertex.
     *
     * @param v underlying vertex
     * @return the y-coordinate or NaN if the vertex does not exist
     */
    public double getVertexPositionY(Vertex v) {
        GraphVertexNode node = vertexNodes.get(v);
        if (node != null) {
            return node.getPositionCenterY();
        }
        return Double.NaN;
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element
     */
    public StylableNode getStylableVertex(Vertex v) {
        return vertexNodes.get(v);
    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param vertexElement underlying vertex's element
     * @return stylable element
     */
    public StylableNode getStylableVertex(String vertexElement) {
        for (Vertex v : vertexNodes.keySet()) {
            if (v.getId().equals(vertexElement)) {
                return vertexNodes.get(v);
            }
        }
        return null;
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edge underlying graph edge
     * @return stylable element
     */
    public StylableNode getStylableEdge(Edge edge) {
        return edgeNodes.get(edge);
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param edgeElement underlying graph edge's element
     * @return stylable element
     */
//    public SmartStylableNode getStylableEdge(Edge edgeElement) {
//        for (Edge e : edgeNodes.keySet()) {
//            if (e.compareTo(edgeElement) == 0) {
//                return edgeNodes.get(e);
//            }
//        }
//        return null;
//    }

    /**
     * Returns the associated stylable element with a graph vertex.
     *
     * @param v underlying vertex
     * @return stylable element (label)
     */
    public StylableNode getStylableLabel(Vertex v) {
        GraphVertexNode vertex = vertexNodes.get(v);

        return vertex != null ? vertex.getStylableLabel() : null;
    }

    /**
     * Returns the associated stylable element with a graph edge.
     *
     * @param e underlying graph edge
     * @return stylable element (label)
     */
    public StylableNode getStylableLabel(Edge e) {
        GraphEdgeBase edge = edgeNodes.get(e);

        return edge != null ? edge.getStylableLabel() : null;
    }


    /**
     * Loads the stylesheet and applies the .graph class to this panel.
     */
    private void loadStylesheet(URI cssFile) {
        try {
            String css;
            if (cssFile != null) {
                css = cssFile.toURL().toExternalForm();
            } else {
                File f = new File("src/main/resources/graph.css");
                css = f.toURI().toURL().toExternalForm();
            }

            getStylesheets().add(css);
            this.getStyleClass().add("graph");
        } catch (MalformedURLException ex) {
            Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Enables the double click action on this pane.
     * <p>
     * This method identifies the node that was clicked and, if any, calls the
     * appropriate consumer, i.e., vertex or edge consumers.
     */
    private void enableMouseListener() {
        setOnMouseClicked((MouseEvent mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
//                System.out.println(mouseEvent);
                if (mouseEvent.getClickCount() == 2) {
                    //no need to continue otherwise
                    if (vertexDoubleClickConsumer == null && edgeDoubleClickConsumer == null) {
                        return;
                    }

                    Node node = pick(GraphPanel.this, mouseEvent.getX(), mouseEvent.getY());
//                    System.out.println(node);

                    if (node instanceof GraphVertex) {
                        GraphVertex v = (GraphVertex) node;
                        vertexDoubleClickConsumer.accept(v);
                    } else if (node instanceof GraphEdge) {
                        GraphEdge e = (GraphEdge) node;
                        edgeDoubleClickConsumer.accept(e);
                    } else {
                        theGraph.addVertex((theGraph.numVertices() + 1) + "", mouseEvent.getX(), mouseEvent.getY());
                    }
                    updateNodes();
                }

                if (mouseEvent.isAltDown() && mouseEvent.getClickCount() == 1) {
//                    System.out.println("Alt down");
                    Node node = pick(GraphPanel.this, mouseEvent.getX(), mouseEvent.getY());
                    if (node == null) {
                        return;
                    }

                    if (node instanceof GraphVertex) {
                        GraphVertex v = (GraphVertex) node;
                        vertexPickConsumer.accept(v);
                        updateNodes();
                    }
                }

                if (mouseEvent.isControlDown() && mouseEvent.getClickCount() == 1) {
                    Node node = pick(GraphPanel.this, mouseEvent.getX(), mouseEvent.getY());
                    if (node == null) {
                        return;
                    }

                    if (node instanceof GraphVertex) {
                        GraphVertex v = (GraphVertex) node;
                        theGraph.removeVertex(v.getUnderlyingVertex());
//                        System.out.println(theGraph.toString());
                        updateNodes();
                    } else if (node instanceof GraphEdge) {
                        GraphEdge e = (GraphEdge) node;
                        theGraph.removeEdge(e.getUnderlyingEdge());
                        update();
                    }
                }
            }
        });
    }

    public void setState(State state) {
        this.state = state;
        updateState();
    }

    private void updateState() {
        Map<Vertex, State.VertexState> vertexStateMap = state.getVertexStateMap();
        Map<Edge, State.EdgeState> edgeStateMap = state.getEdgeStateMap();
        Map<Vertex, Double> distanceMap = state.getDistanceMap();
        for (Vertex v : vertexStateMap.keySet()) {
            GraphVertexNode vv = vertexNodes.get(v);
            if (vv == null)
                continue;

            State.VertexState state = vertexStateMap.get(v);
            vv.setState(state);
        }

        for (Edge e : edgeStateMap.keySet()) {
            GraphEdgeBase ev = edgeNodes.get(e);
            if (ev == null)
                continue;

            State.EdgeState state = edgeStateMap.get(e);
            ev.setState(state);
        }

        for (Vertex v : distanceMap.keySet()) {
            GraphVertexNode vv = vertexNodes.get(v);
            if (vv == null)
                continue;

            double distance = distanceMap.get(v);
            if (distance != Double.MAX_VALUE) {
                Label distanceLabel = vv.getDistanceLabel();
                distanceLabel.setText(String.format("%.1f", distance));
            }
        }
    }

    public void setDefaultState() {
        this.state = createDefaultState();
    }

    /**
     * Represents a tuple in Java.
     *
     * @param <T> the type of the tuple
     */
    private class Tuple<T> {

        private final T first;
        private final T second;

        public Tuple(T first, T second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.first);
            hash = 29 * hash + Objects.hashCode(this.second);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tuple<?> other = (Tuple<?>) obj;
            if (!Objects.equals(this.first, other.first)) {
                return false;
            }
            if (!Objects.equals(this.second, other.second)) {
                return false;
            }
            return true;
        }
    }

}
