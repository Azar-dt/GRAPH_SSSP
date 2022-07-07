package com.graph.graph.graphcore;

public class Vertex implements Comparable<Vertex> {
    private String id; // id of the vertex

    private double positionX;
    private double positionY;

    public Vertex(String id) {
        this(id, 0, 0);
    }

    public Vertex(String id, double positionX, double positionY) {
        this.id = id;
        if (positionX == 0 && positionY == 0) {
            this.positionX = Math.random() * 100;
            this.positionY = Math.random() * 100;
        } else {
            this.positionX = positionX;
            this.positionY = positionY;
        }
    }

    public void setPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Vertex o) {
        return this.id.compareTo(o.getId());
    }

}
