package com.graph.graph.step;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class State {
    private Map<Vertex, VertexState> vertexStateMap;
    private Map<Edge, EdgeState> edgeStateMap;
    private Map<Vertex, Double> distanceMap;

    public State(List<Vertex> vertexList, List<Edge> edgeList, List<Vertex> vertexHighlighted, List<Edge> edgeHighlighted, List<Vertex> vertexTraversed, List<Edge> edgeTraversed, List<Vertex> vertexQueued, List<Edge> uselessEdges, Map<Vertex, Double> distance) {
        vertexStateMap = new HashMap<Vertex, VertexState>();
        edgeStateMap = new HashMap<Edge, EdgeState>();
        distanceMap = new HashMap<Vertex, Double>();
        boolean isDisable = !(vertexHighlighted == null && edgeHighlighted == null && vertexTraversed == null && edgeTraversed == null && vertexQueued == null);

        for (Vertex v : vertexList) {
            vertexStateMap.put(v, new VertexState(v, VERTEX_STATE.DEFAULT));
            distanceMap.put(v, Double.MAX_VALUE);
        }

        for (Edge e : edgeList) {
            edgeStateMap.put(e, new EdgeState(e, EDGE_STATE.DEFAULT));
        }

        if (distance != null) {
            for (Vertex v : distance.keySet()) {
                if (distance.get(v) != null) {
                    distanceMap.put(v, distance.get(v));
                }
            }
        }

        if (vertexQueued != null)
            for (Vertex v : vertexQueued) {
                vertexStateMap.get(v).setState(VERTEX_STATE.UNQUEUED);
            }

        if (vertexTraversed != null)
            for (Vertex v : vertexTraversed) {
                vertexStateMap.get(v).setState(VERTEX_STATE.TRAVERSED);
            }

        if (edgeTraversed != null)
            for (Edge e : edgeTraversed) {
                edgeStateMap.get(e).setState(EDGE_STATE.TRAVERSED);
            }

        if (vertexHighlighted != null) {
            for (Vertex v : vertexHighlighted) {
                vertexStateMap.get(v).setState(VERTEX_STATE.HIGHLIGHTED);
            }
        }

        if (edgeHighlighted != null) {
            for (Edge e : edgeHighlighted) {
                edgeStateMap.get(e).setState(EDGE_STATE.HIGHLIGHTED);
            }
        }

        if (uselessEdges != null) {
            for (Edge e : uselessEdges) {
                edgeStateMap.get(e).setState(EDGE_STATE.USELESS);
            }
        }
    }

    public State(List<Vertex> vertexList, List<Edge> edgeList) {
        this(vertexList, edgeList, null, null, null, null, null, null, null);
    }

    public static class EdgeState {
        Edge edge;
        EDGE_STATE state;

        public EdgeState(Edge edge, EDGE_STATE state) {
            this.edge = edge;
            this.state = state;
        }

        public EDGE_STATE getState() {
            return state;
        }

        void setState(EDGE_STATE state) {
            this.state = state;
        }

        public Edge getEdge() {
            return edge;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((edge == null) ? 0 : edge.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EdgeState other = (EdgeState) obj;
            if (edge == null) {
                if (other.edge != null)
                    return false;
            } else if (!edge.equals(other.edge))
                return false;
            if (state != other.state)
                return false;
            return true;
        }
    }

    public static class VertexState {
        Vertex vertex;
        VERTEX_STATE state;

        public VertexState(Vertex vertex, VERTEX_STATE state) {
            this.vertex = vertex;
            this.state = state;
        }

        public VERTEX_STATE getState() {
            return state;
        }

        public void setState(VERTEX_STATE state) {
            this.state = state;
        }

        public Vertex getVertex() {
            return vertex;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((vertex == null) ? 0 : vertex.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VertexState other = (VertexState) obj;
            if (vertex == null) {
                if (other.vertex != null)
                    return false;
            } else if (!vertex.equals(other.vertex))
                return false;
            if (state != other.state)
                return false;
            return true;
        }
    }

    public static enum VERTEX_STATE {
        DEFAULT, // default state
        HIGHLIGHTED, // highlighted state
        TRAVERSED, // traversed state
        UNQUEUED // queued state

    }

    public static enum EDGE_STATE {
        DEFAULT, // default state
        HIGHLIGHTED, // highlighted state
        TRAVERSED, // traversed state
        USELESS, // queued state

    }

    public Map<Vertex, VertexState> getVertexStateMap() {
        return vertexStateMap;
    }

    public Map<Edge, EdgeState> getEdgeStateMap() {
        return edgeStateMap;
    }

    public Map<Vertex, Double> getDistanceMap() {
        return distanceMap;
    }
}
