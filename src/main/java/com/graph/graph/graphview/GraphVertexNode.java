package com.graph.graph.graphview;

import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GraphVertexNode extends Circle implements GraphVertex, LabelledNode {

    private final Vertex underlyingVertex;
    /* Critical for performance, so we don't rely on the efficiency of the Graph.areAdjacent method */
    private final Set<GraphVertexNode> adjacentVertices;

    private Label attachedLabel = null;

    private Label detailLabel = null;
    private boolean isDragging = false;

    private State.VertexState state;
    /*
    Automatic layout functionality members
     */
    private final PointVector forceVector = new PointVector(0, 0);
    private final PointVector updatedPosition = new PointVector(0, 0);

    /* Styling proxy */
    private final StyleProxy styleProxy;


    /**
     * Constructor which sets the instance attributes.
     *
     * @param v         the underlying vertex
     * @param radius    radius of this vertex representation, i.e., a circle
     * @param allowMove should the vertex be draggable with the mouse
     */

    public GraphVertexNode(Vertex v, double radius, boolean allowMove) {
        this(v, radius, allowMove, null);
    }

    public GraphVertexNode(Vertex v, double radius, boolean allowMove, State.VertexState state) {
        super(v.getPositionX(), v.getPositionY(), radius);
        double x = v.getPositionX();
        double y = v.getPositionY();


        this.underlyingVertex = v;
//        this.attachedLabel = new Label(x, y, v.getId());
        this.detailLabel = new Label(x, y, v.getId());
        this.isDragging = false;

        this.adjacentVertices = new HashSet<>();

        styleProxy = new StyleProxy(this);
        styleProxy.addStyleClass("vertex");

        if (allowMove) {
            enableDrag();
        }
        this.state = state != null ? state : new State.VertexState(v, State.VERTEX_STATE.DEFAULT);
        updateState();
    }

    public void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    /**
     * Adds a vertex to the internal list of adjacent vertices.
     *
     * @param v vertex to add
     */
    public void addAdjacentVertex(GraphVertexNode v) {
        this.adjacentVertices.add(v);
    }

    /**
     * Removes a vertex from the internal list of adjacent vertices.
     *
     * @param v vertex to remove
     * @return true if <code>v</code> existed; false otherwise.
     */
    public boolean removeAdjacentVertex(GraphVertexNode v) {
        return this.adjacentVertices.remove(v);
    }

    /**
     * Removes a collection of vertices from the internal list of adjacent
     * vertices.
     *
     * @param col collection of vertices
     * @return true if any vertex was effectively removed
     */
    public boolean removeAdjacentVertices(Collection<GraphVertexNode> col) {
        return this.adjacentVertices.removeAll(col);
    }

    /**
     * Checks whether <code>v</code> is adjacent this instance.
     *
     * @param v vertex to check
     * @return true if adjacent; false otherwise
     */
    public boolean isAdjacentTo(GraphVertexNode v) {
        return this.adjacentVertices.contains(v);
    }

    /**
     * Returns the current position of the instance in pixels.
     *
     * @return the x,y coordinates in pixels
     */
    public Point2D getPosition() {
        return new Point2D(getCenterX(), getCenterY());
    }

    /**
     * Sets the position of the instance in pixels.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    @Override
    public void setPosition(double x, double y) {
        if (isDragging) {
            return;
        }

        setCenterX(x);
        setCenterY(y);
    }

    @Override
    public double getPositionCenterX() {
        return getCenterX();
    }

    @Override
    public double getPositionCenterY() {
        return getCenterY();
    }


    /**
     * Sets the position of the instance in pixels.
     *
     * @param p coordinates
     */
    public void setPosition(Point2D p) {
        setPosition(p.getX(), p.getY());
    }

    /**
     * Resets the current computed external force vector.
     */
    public void resetForces() {
        forceVector.x = forceVector.y = 0;
        updatedPosition.x = getCenterX();
        updatedPosition.y = getCenterY();
    }

    /**
     * Adds the vector represented by <code>(x,y)</code> to the current external
     * force vector.
     *
     * @param x x-component of the force vector
     * @param y y-component of the force vector
     */
    public void addForceVector(double x, double y) {
        forceVector.x += x;
        forceVector.y += y;
    }

    /**
     * Returns the current external force vector.
     *
     * @return force vector
     */
    public Point2D getForceVector() {
        return new Point2D(forceVector.x, forceVector.y);
    }

    /**
     * Returns the future position of the vertex.
     *
     * @return future position
     */
    public Point2D getUpdatedPosition() {
        return new Point2D(updatedPosition.x, updatedPosition.y);
    }

    /**
     * Updates the future position according to the current internal force
     * vector.
     * <p>
     * see SmartGraphPanel#updateForces()
     */
    public void updateDelta() {
        updatedPosition.x = updatedPosition.x /* + speed*/ + forceVector.x;
        updatedPosition.y = updatedPosition.y + forceVector.y;
    }

    /**
     * Moves the vertex position to the computed future position.
     * <p>
     * Moves are constrained within the parent pane dimensions.
     * <p>
     * see SmartGraphPanel#applyForces()
     */
    public void moveFromForces() {

        //limit movement to parent bounds
        double height = getParent().getLayoutBounds().getHeight();
        double width = getParent().getLayoutBounds().getWidth();

        updatedPosition.x = boundCenterCoordinate(updatedPosition.x, 0, width);
        updatedPosition.y = boundCenterCoordinate(updatedPosition.y, 0, height);

        setPosition(updatedPosition.x, updatedPosition.y);
    }

    /**
     * Make a node movable by dragging it around with the mouse primary button.
     */
    private void enableDrag() {
        final PointVector dragDelta = new PointVector(0, 0);

        setOnMousePressed((MouseEvent mouseEvent) -> {
            if (mouseEvent.isPrimaryButtonDown() && !mouseEvent.isAltDown()) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
                isDragging = true;

                mouseEvent.consume();
            }

        });

        setOnMouseReleased((MouseEvent mouseEvent) -> {
            getScene().setCursor(Cursor.HAND);
            isDragging = false;

            mouseEvent.consume();
        });

        setOnMouseDragged((MouseEvent mouseEvent) -> {
            if (mouseEvent.isPrimaryButtonDown() && !mouseEvent.isAltDown()) {
                double newX = mouseEvent.getX() + dragDelta.x;
                double x = boundCenterCoordinate(newX, 0, getParent().getLayoutBounds().getWidth());
                setCenterX(x);
                double newY = mouseEvent.getY() + dragDelta.y;
                double y = boundCenterCoordinate(newY, 0, getParent().getLayoutBounds().getHeight());
                setCenterY(y);
                mouseEvent.consume();
            }

        });

        setOnMouseEntered((MouseEvent mouseEvent) -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }

        });

        setOnMouseExited((MouseEvent mouseEvent) -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }

        });
    }

    private double boundCenterCoordinate(double value, double min, double max) {
        double radius = getRadius();

        if (value < min + radius) {
            return min + radius;
        } else if (value > max - radius) {
            return max - radius;
        } else {
            return value;
        }
    }

    @Override
    public void attachLabel(Label label) {
        this.attachedLabel = label;
        label.xProperty().bind(centerXProperty().subtract(label.getLayoutBounds().getWidth() / 2.0));
        label.yProperty().bind(centerYProperty().add(getRadius() + label.getLayoutBounds().getHeight()));
    }

    public void attachDetailLabel(Label label) {
        this.detailLabel = label;
        label.xProperty().bind(centerXProperty().subtract(label.getLayoutBounds().getWidth() / 2.0));
        label.yProperty().bind(centerYProperty().add(getRadius() + label.getLayoutBounds().getHeight() / 4.0));
    }

    @Override
    public Label getAttachedLabel() {
        return attachedLabel;
    }

    @Override
    public Vertex getUnderlyingVertex() {
        return underlyingVertex;
    }


    @Override
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        styleProxy.addStyleClass(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return styleProxy.removeStyleClass(cssClass);
    }

    @Override
    public StylableNode getStylableLabel() {
        return this.attachedLabel;
    }

    /**
     * Internal representation of a 2D point or vector for quick access to its
     * attributes.
     */
    private class PointVector {

        double x, y;

        public PointVector(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public void setState(State.VertexState state) {
        if (!this.state.equals(state)) {
            this.state = state;
            updateState();
        }
    }

    private void updateState() {
        if (state == null)
            return;
        switch (state.getState()) {
            case DEFAULT:
                removeStyleClass("vertex-highlighted");
                removeStyleClass("vertex-unqueued");
                removeStyleClass("vertex-traversed");
                if (attachedLabel != null) {
                    attachedLabel.removeStyleClass("vertex-label-highlighted");
                    attachedLabel.removeStyleClass("vertex-label-unqueued");
                    attachedLabel.removeStyleClass("vertex-label-traversed");
                }
                break;
            case HIGHLIGHTED:
                removeStyleClass("vertex-unqueued");
                removeStyleClass("vertex-traversed");
                addStyleClass("vertex-highlighted");
                if (attachedLabel != null) {
                    attachedLabel.removeStyleClass("vertex-label-unqueued");
                    attachedLabel.removeStyleClass("vertex-label-traversed");
                    attachedLabel.addStyleClass("vertex-label-highlighted");
                }
                break;
            case UNQUEUED:
                removeStyleClass("vertex-highlighted");
                removeStyleClass("vertex-traversed");
                addStyleClass("vertex-unqueued");
                if (attachedLabel != null) {
                    attachedLabel.removeStyleClass("vertex-label-highlighted");
                    attachedLabel.removeStyleClass("vertex-label-traversed");
                    attachedLabel.addStyleClass("vertex-label-unqueued");
                }
                break;
            case TRAVERSED:
                removeStyleClass("vertex-unqueued");
                removeStyleClass("vertex-highlighted");
                addStyleClass("vertex-traversed");
                if (attachedLabel != null) {
                    attachedLabel.removeStyleClass("vertex-label-highlighted");
                    attachedLabel.removeStyleClass("vertex-label-unqueued");
                    attachedLabel.addStyleClass("vertex-label-traversed");
                }
                break;
            default:
                break;
        }
    }
}
