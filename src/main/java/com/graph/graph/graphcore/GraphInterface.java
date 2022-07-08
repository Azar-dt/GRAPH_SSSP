package com.graph.graph.graphcore;

import java.util.Collection;

public interface GraphInterface {

    /**
     * Returns the total number of vertices of the graph.
     *
     * @return total number of vertices
     */
    public int numVertices();

    /**
     * Returns the total number of edges of the graph.
     *
     * @return total number of vertices
     */
    public int numEdges();

    /**
     * Returns the vertices of the graph as a collection.
     * <p>
     * If there are no vertices, returns an empty collection.
     *
     * @return collection of vertices
     */
    public Collection<Vertex> vertices();

    /**
     * Returns the edges of the graph as a collection.
     * <p>
     * If there are no edges, returns an empty collection.
     *
     * @return collection of edges
     */
    public Collection<Edge> edges();

    /**
     * Returns a vertex's <i>incident</i> edges as a collection.
     * <p>
     * Incident edges are all edges that are connected to vertex <code>v</code>.
     * If there are no incident edges, e.g., an isolated vertex,
     * returns an empty collection.
     *
     * @param v vertex for which to obtain the incident edges
     * @return collection of edges
     */
    public Collection<Edge> incidentEdges(Vertex v) throws InvalidVertexException;

    /**
     * Given vertex <code>v</code>, return the opposite vertex at the other end
     * of edge <code>e</code>.
     * <p>
     * If both <code>v</code> and <code>e</code> are valid, but <code>e</code>
     * is not connected to <code>v</code>, returns <i>null</i>.
     *
     * @param v vertex on one end of <code>e</code>
     * @param e edge connected to <code>v</code>
     * @return opposite vertex along <code>e</code>
     * @throws InvalidVertexException if the vertex is invalid for the graph
     * @throws InvalidEdgeException   if the edge is invalid for the graph
     */
    public Vertex opposite(Vertex v, Edge e) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Evaluates whether two vertices are adjacent, i.e., there exists some
     * edge connecting <code>u</code> and <code>v</code>.
     *
     * @param u a vertex
     * @param v another vertex
     * @return true if they are adjacent, false otherwise.
     * @throws InvalidVertexException if <code>u</code> or <code>v</code>
     *                                are invalid vertices for the graph
     */
    public boolean areAdjacent(Vertex u, Vertex v) throws InvalidVertexException;

    /**
     * Inserts a new vertex with a given element, returning its reference.
     *
     * @param vElement the element to store at the vertex
     * @return the reference of the newly created vertex
     * @throws InvalidVertexException if there already exists a vertex
     *                                containing <code>vElement</code>
     *                                according to the equality of
     *                                {@link Object#equals(java.lang.Object) }
     *                                method.
     */
    public Vertex insertVertex(String vElement) throws InvalidVertexException;

    /**
     * Inserts a new edge with a given element between two existing vertices and
     * return its (the edge's) reference.
     *
     * @param u a vertex
     * @param v another vertex
     * @return the reference for the newly created edge
     * @throws InvalidVertexException if <code>u</code> or <code>v</code>
     *                                are invalid vertices for the graph
     * @throws InvalidEdgeException   if there already exists an edge
     *                                containing <code>edgeElement</code>
     *                                according to the equality of
     *                                {@link Object#equals(java.lang.Object) }
     *                                method.
     */
    public Edge insertEdge(Vertex u, Vertex v) throws InvalidVertexException, InvalidEdgeException;


    /**
     * Removes a vertex, along with all of its incident edges, and returns the element
     * stored at the removed vertex.
     *
     * @param v vertex to remove
     * @return element stored at the removed vertex
     * @throws InvalidVertexException if <code>v</code> is an invalid vertex for the graph
     */
    public String removeVertex(Vertex v) throws InvalidVertexException;

    /**
     * Removes an edge and return its element.
     *
     * @param e edge to remove
     * @return element stored at the removed edge
     * @throws InvalidEdgeException if <code>e</code> is an invalid edge for the graph.
     */
    public void Boolean(Edge e) throws InvalidEdgeException;

    /**
     * Replaces the element of a given vertex with a new element and returns the
     * previous element stored at <code>v</code>.
     *
     * @param v          vertex to replace its element
     * @param newElement new element to store in <code>v</code>
     * @return previous element previously stored in <code>v</code>
     * @throws InvalidVertexException if the vertex <code>v</code> is invalid for the graph, or;
     *                                if there already exists another vertex containing
     *                                the element <code>newElement</code>
     *                                according to the equality of
     *                                {@link Object#equals(java.lang.Object) }
     *                                method.
     */
    public String replace(Vertex v, String newElement) throws InvalidVertexException;

}

