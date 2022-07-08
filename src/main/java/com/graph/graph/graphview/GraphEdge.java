package com.graph.graph.graphview;

import com.graph.graph.graphcore.Edge;

public interface GraphEdge extends StylableNode {

    /**
     * Returns the underlying (stored reference) graph edge.
     *
     * @return edge reference
     * @see GraphPanel
     */
    public Edge getUnderlyingEdge();

    /**
     * Returns the attached arrow of the edge, for styling purposes.
     * <p>
     * The arrows are only used with directed graphs.
     *
     * @return arrow reference; null if does not exist.
     */
    public StylableNode getStylableArrow();

    /**
     * Returns the label node for further styling.
     *
     * @return the label node.
     */
    public StylableNode getStylableLabel();
}

