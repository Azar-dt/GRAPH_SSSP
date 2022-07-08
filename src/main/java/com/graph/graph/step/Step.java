package com.graph.graph.step;

public class Step {
    private Integer id;
    private String description;

    private State state;

    public Step(Integer id, String description, State state) {
        this.id = id;
        this.description = description;
        this.state = state;
    }

    public String toString() {
        return "Step " + id + ": " + description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
