package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.Step;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {
    protected Graph graph;
    protected List<Step> stepList = new ArrayList<>();
    protected Vertex startVertex;

    public abstract void run();

    public abstract void showStep();

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
}
