package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;
import com.graph.graph.utils.PressEnterToContinue;

import java.io.IOException;
import java.util.*;

public class BFS extends Algorithm {
    public BFS() {
        super();
        // init pseudoStep
        pseudoStep.clear();
        pseudoStep.put(0, "initSSSP, Q.push(sourceVertex)");
        pseudoStep.put(1, "while Q is not empty // Q is normal queue");
        pseudoStep.put(2, "for each neighbor v of u = Q.front(), Q.pop() \n" + "if v is not visited");
        pseudoStep.put(3, "parent(v) = u, visited(v) = true, Q.push(v), save distance\n");
    }

    @Override
    public void run() {
        stepList.clear(); // clear stepList everytime start run algorithm
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------BFS-------------");
        Queue<Vertex> queue = new LinkedList<>();
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        HashMap<Vertex, Vertex> parent = new HashMap<>();
        HashMap<Vertex, Integer> distance = new HashMap<>();
        Integer currentDistance = 0;

        // for save step
        List<Vertex> verticesHighlighted = new LinkedList<>();
        List<Edge> edgesHighlighted = new LinkedList<>();
        List<Vertex> verticesTraversed = new LinkedList<>();
        List<Edge> edgesTraversed = new LinkedList<>();
        List<Vertex> vertexQueued = new LinkedList<>();
        List<Vertex> vertexList = new ArrayList<>(graph.getVertices());
        List<Edge> edgeList = new ArrayList<>(graph.getEdges());
        State state; // save state of vertex and edge
        String description; // save description of vertex and edge

        // for algorithm
        queue.add(startVertex);
        visited.put(startVertex, true);
        distance.put(startVertex, currentDistance);

        // step 0
        verticesHighlighted.add(startVertex);
        description = "0 is the source vertex.\n" + "Set parent[v] = -1, d[" + startVertex.getId() + "] = 0 and push this vertex to queue.";
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, startVertex);
        stepList.add(new Step(0, description, state));
        // done step 0

        verticesHighlighted.remove(startVertex);
        vertexQueued.add(startVertex);
        // core algorithm
        while (queue.size() > 0) {
            currentDistance++;

            Vertex vertex = queue.remove();
            verticesHighlighted.add(vertex);
            verticesTraversed.add(vertex);
            vertexQueued.remove(vertex);

            StringBuilder sb = new StringBuilder();
            for (Vertex v : queue) {
                sb.append(v.getId() + " ");
            }
            description = "The queue is : { " + sb.toString() + "}";
            state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, vertex);
            stepList.add(new Step(1, description, state));


            Set<Vertex> neighbors = graph.getNeighbors(vertex.getId());
            for (Vertex v : neighbors) {
                StringBuilder step2 = new StringBuilder();
                step2.append("Explore neighbors of " + vertex.getId() + "\n Current neighbor : " + v.getId());
                step2.append("\n");
                step2.append("Visited vertex : {");
                // visited to string
                for (Vertex visit : visited.keySet()) {
                    step2.append(visit.getId() + " ");
                }
                step2.append("}");
                description = step2.toString();

                Edge currentEdge = graph.getEdge(vertex.getId(), v.getId());
                edgesHighlighted.add(currentEdge);
                state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, vertex);
                stepList.add(new Step(2, description, state));
                edgesTraversed.add(currentEdge);
                if (!visited.containsKey(v)) {
                    parent.put(v, vertex); // save the parent of v
                    queue.add(v);
                    visited.put(v, true);
                    distance.put(v, currentDistance); // save distance of v to source
                    description = "Push " + v.getId() + " to queue.\n" + "Set parent[v] = " + vertex.getId() + ", d[" + v.getId() + "] = " + currentDistance;
                    edgesHighlighted.remove(currentEdge);
                    vertexQueued.add(v);
//                    verticesTraversed.add(v);
                    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, vertex);
                    stepList.add(new Step(3, description, state));
                } else {
                    description = "The vertex " + v.getId() + " is already visited.\n";
                    edgesHighlighted.remove(currentEdge);
                    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, vertex);
                    stepList.add(new Step(3, description, state));
                }
            }
            verticesHighlighted.remove(vertex);
        }
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, null);
        stepList.add(new Step(4, "End of BFS", state));
    }

    @Override
    public void showStep() {
        for (Step step : stepList) {
            System.out.println(step.toString());
            System.out.println("----------------------------");
            for (int i = 0; i < pseudoStep.size(); i++) {
                if (step.getId() == i) {
                    System.out.println(ANSI_YELLOW + pseudoStep.get(i) + ANSI_RESET);
                } else {
                    System.out.println(pseudoStep.get(i));
                }
            }
            PressEnterToContinue.run();
        }
    }
}

