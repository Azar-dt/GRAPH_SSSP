package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Algorithm {
    protected Graph graph;
    protected HashMap<Integer, String> pseudoStep = new HashMap<Integer, String>();
    protected List<Step> stepList = new ArrayList<>();
    protected HashMap<Vertex, Double> distance = new HashMap<>();
    protected HashMap<Vertex, Vertex> parent = new HashMap<>();
    protected Vertex startVertex;

    // Declaring ANSI_RESET so that we can reset the color
    protected String ANSI_RESET = "\u001B[0m";

    // Declaring the color
    // Custom declaration
    protected String ANSI_YELLOW = "\u001B[33m";

    public abstract void run();

    public abstract void showStep();

    public HashMap<Integer, String> getPseudoStep() {
        return pseudoStep;
    }

    public void setPseudoStep(HashMap<Integer, String> pseudoStep) {
        this.pseudoStep = pseudoStep;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public HashMap<Vertex, Double> getDistance() {
        return distance;
    }

    public HashMap<Vertex, Vertex> getParent() {
        return parent;
    }
}
