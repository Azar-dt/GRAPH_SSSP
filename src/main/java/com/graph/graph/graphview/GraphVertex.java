package com.graph.graph.graphview;

import com.graph.graph.graphcore.Vertex;

public interface GraphVertex extends StylableNode {

    /**
     * Returns the underlying (stored reference) graph vertex.
     *
     * @return vertex reference
     * @see com.graph.graph.graphcore.Graph
     */
    public Vertex getUnderlyingVertex();

    /**
     * Sets the position of this vertex in panel coordinates.
     * <p>
     * Apart from its usage in the {@link GraphPanel}, this method
     * should only be called when implementing @link SmartPlacementStrategy.
     *
     * @param x x-coordinate for the vertex
     * @param y y-coordinate for the vertex
     */
    public void setPosition(double x, double y);

    /**
     * Return the center x-coordinate of this vertex in panel coordinates.
     *
     * @return x-coordinate of the vertex
     */
    public double getPositionCenterX();

    /**
     * Return the center y-coordinate of this vertex in panel coordinates.
     *
     * @return y-coordinate of the vertex
     */
    public double getPositionCenterY();

    /**
     * Returns the circle radius used to represent this vertex.
     *
     * @return circle radius
     */
    public double getRadius();

    /**
     * Returns the label node for further styling.
     *
     * @return the label node.
     */
    public StylableNode getStylableLabel();
}