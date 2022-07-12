package com.graph.graph.graphcore;

public class Edge implements Comparable<Edge> {
    private Vertex source;
    private Vertex destination;
    private double weight;

    public Edge(Vertex source, Vertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge o) {
        if (this.source.getId().equals(o.getSource().getId())) {
            return this.destination.getId().compareTo(o.getDestination().getId());
        } else {
            return this.source.getId().compareTo(o.getSource().getId());
        }
    }

    public Vertex[] vertices() {
        return new Vertex[]{source, destination};
    }
}
