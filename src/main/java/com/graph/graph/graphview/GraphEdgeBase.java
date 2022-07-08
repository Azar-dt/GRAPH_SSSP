package com.graph.graph.graphview;

public interface GraphEdgeBase extends GraphEdge, LabelledNode {

    /**
     * Attaches a {@link Arrow} to this edge, binding its position/rotation.
     *
     * @param arrow arrow to attach
     */
    public void attachArrow(Arrow arrow);

    /**
     * Returns the attached {@link Arrow}, if any.
     *
     * @return reference of the attached arrow; null if none.
     */
    public Arrow getAttachedArrow();

}
