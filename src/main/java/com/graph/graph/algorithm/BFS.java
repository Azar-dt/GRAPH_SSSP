package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;

import java.io.IOException;
import java.util.*;

public class BFS extends Algorithm {
    public BFS() {
        super();
        // init pseudoStep
        pseudoStep.clear();
        pseudoStep.put(0, "show warning if the graph is weighted");
        pseudoStep.put(1, "initSSSP, Q.push(sourceVertex)");
        pseudoStep.put(2, "while Q is not empty // Q is normal queue");
        pseudoStep.put(3, "    u = Q.front(), Q.pop() ");
        pseudoStep.put(4, "    for each neighbor v of u \n" + "    if v is not visited");
        pseudoStep.put(5, "        parent(v) = u, visited(v) = true, Q.push(v), save distance\n");
        pseudoStep.put(6, "End of BFS");
    }

    @Override
    public void run() {
        stepList.clear(); // clear stepList everytime start run algorithm
        Scanner sc = new Scanner(System.in);
        System.out.println("BFS algorithm");
        Queue<Vertex> queue = new LinkedList<>();
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        Double currentDistance = 0.0;
        String wariningmsg = "";

        // for save step
        List<Vertex> verticesHighlighted = new LinkedList<>();
        List<Edge> edgesHighlighted = new LinkedList<>();
        List<Vertex> verticesTraversed = new LinkedList<>();
        List<Edge> edgesTraversed = new LinkedList<>();
        List<Vertex> vertexQueued = new LinkedList<>();
        List<Edge> uselessEdges = new LinkedList<>();
        List<Vertex> vertexList = new ArrayList<>(graph.getVertices());
        List<Edge> edgeList = new ArrayList<>(graph.getEdges());
        State state; // save state of vertex and edge
        String description; // save description of vertex and edge

        //check if bfs is possible
        for (Edge edge : edgeList) {
            if (edge.getWeight() != 1.0) {
                wariningmsg = "WARNING: The graph is not an unweighted/constant-weighted graph.\n" + "BFS will likely yield wrong SSSP answer.";
                break;
            }
        }
        if (wariningmsg.length() > 0) {
            stepList.add(new Step(0, wariningmsg, new State(vertexList, edgeList)));
        }
        // for algorithm
        queue.add(startVertex);
        visited.put(startVertex, true);
        distance.put(startVertex, currentDistance);


        vertexQueued.add(startVertex);
        description = "0 is the source vertex.\n" + "Set parent[v] = -1, d[" + startVertex.getId() + "] = 0 and push this vertex to queue.";
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
        stepList.add(new Step(1, description + wariningmsg, state));
        // done step 0
        // core algorithm
        while (queue.size() > 0) {
            currentDistance++;
            StringBuilder sb = new StringBuilder();
            for (Vertex v : queue) {
                sb.append(v.getId() + " ");
            }
            description = "The queue is : { " + sb.toString() + "}";
            state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
            stepList.add(new Step(2, description, state));

            Vertex vertex = queue.remove();
            verticesHighlighted.add(vertex);
            verticesTraversed.add(vertex);
            vertexQueued.remove(vertex);
            description = "Current Vertex is " + vertex.getId();
            state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
            stepList.add(new Step(3, description, state));

            Set<Vertex> neighbors = graph.getNeighbors(vertex.getId());
            for (Vertex v : neighbors) {
                StringBuilder step2 = new StringBuilder();
                step2.append("Explore neighbors of " + vertex.getId() + "\nCurrent neighbor : " + v.getId());
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
                state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                stepList.add(new Step(4, description, state));
                edgesTraversed.add(currentEdge);
                if (!visited.containsKey(v)) {
                    parent.put(v, vertex); // save the parent of v
                    queue.add(v);
                    visited.put(v, true);
                    distance.put(v, distance.get(vertex) + 1); // save distance of v to source
                    description = "Push " + v.getId() + " to queue.\n" + "Set parent[v] = " + vertex.getId() + ", d[" + v.getId() + "] = " + currentDistance;
                    edgesHighlighted.remove(currentEdge);
                    vertexQueued.add(v);
//                    verticesTraversed.add(v);
                    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                    stepList.add(new Step(5, description, state));
                } else {
                    description = "The vertex " + v.getId() + " is already visited.\n" + "No change";
                    edgesHighlighted.remove(currentEdge);
                    uselessEdges.add(currentEdge);
                    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                    stepList.add(new Step(4, description, state));
                }
            }
            verticesHighlighted.remove(vertex);
        }
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
        stepList.add(new Step(6, "End of BFS", state));
    }

}

