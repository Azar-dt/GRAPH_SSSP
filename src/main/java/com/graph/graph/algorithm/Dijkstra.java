package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;

import java.io.IOException;
import java.util.*;

public class Dijkstra extends Algorithm {
    public Dijkstra() {
        super();
        // init pseudoStep
        pseudoStep.clear();
        pseudoStep.put(0, "show warning if the graph has negative weighted");
        pseudoStep.put(1, "initSSSP, PQ.push(0, sourceVertex)");
        pseudoStep.put(2, "while PQ is not empty // PQ is Priority queue");
        pseudoStep.put(3, "    u = PQ.front(), PQ.pop() ");
        pseudoStep.put(4, "    for each neighbor v of u if u is valid\n\trelax(u,v,w(u,v)) + insert new pair to PQ");
        pseudoStep.put(5, "End of Dijkstra");
    }

    @Override
    public void run() {
        stepList.clear(); // clear stepList everytime start run algorithm
        Scanner sc = new Scanner(System.in);
        System.out.println("Dijkstra algorithm");
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

        //check if Dijkstra is possible
        for (Edge edge : edgeList) {
            if (edge.getWeight() < 0.0) {
                wariningmsg = "WARNING: The graph is a negative-weighted graph.\n" + "Dijkstra will likely yield wrong SSSP answer.";
                break;
            }
        }
        if (wariningmsg.length() > 0) {
            stepList.add(new Step(0, wariningmsg, new State(vertexList, edgeList)));
        }

        // for algorithm
        queue.add(startVertex);
        visited.put(startVertex, true);
        distance.replaceAll( (k,v)-> v=999.0 );
        distance.put(startVertex, currentDistance);

        // step 0
        vertexQueued.add(startVertex);
        description = startVertex.getId()+"is the source vertex.\n" + "Set parent[v] = -1, d[v] = Inf, but d["+startVertex.getId()+"] = 0, PQ ={...}";
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
        stepList.add(new Step(1, description, state));
        // done step 0
        // core algorithm
        while (queue.size() > 0) {
            //currentDistance++;
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
            description = "Current Vertex is (" + vertex.getId()+", " + distance.get(vertex)+")";
            state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
            stepList.add(new Step(3, description, state));

            Set<Vertex> neighbors = graph.getNeighbors(vertex.getId());
            for (Vertex v : neighbors) {
                Double tmpDis = 0.0;
                StringBuilder step2 = new StringBuilder();
                step2.append("Explore neighbors of vertex u =" + vertex.getId() + ", d["+vertex.getId()+"] ="+distance.get(vertex)+"\nCurrent neighbor : " + v.getId());
                step2.append("\n");
                step2.append("Visited vertex : {");
                // visited to string
                for (Vertex visit : visited.keySet()) {
                    step2.append(visit.getId() + " ");
                }
                step2.append("}");
                description = step2.toString();

                Edge currentEdge = graph.getEdge(vertex.getId(), v.getId());
                tmpDis = currentEdge.getWeight();
                edgesHighlighted.add(currentEdge);
                state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                stepList.add(new Step(4, description, state));
                edgesTraversed.add(currentEdge);
                if (!visited.containsKey(v)) {
                    parent.put(v, vertex); // save the parent of v
                    queue.add(v);
                    visited.put(v, true);
                    distance.put(v, distance.get(vertex)+tmpDis); // save distance of v to source
                    description = "Not visited\n => Push " + v.getId() + " to queue.\n" + "Set parent["+v.getId()+"] = " + vertex.getId() + ", d[" + v.getId() + "] = " + distance.get(v);
                    edgesHighlighted.remove(currentEdge);
                    vertexQueued.add(v);
//                    verticesTraversed.add(v);
                    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                    stepList.add(new Step(4, description, state));
                } else {
                    // v is visited
                    // if d[v] is <= d[u] + w[u,v] => no change
                    if (distance.get(v) <= (distance.get(vertex)+currentEdge.getWeight())) {
                        description = "d["+v.getId()+"] is <= d["+vertex.getId()+"] + w[u,v]\n" + "=> No change";
                        edgesHighlighted.remove(currentEdge);
                        uselessEdges.add(currentEdge);
                        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                        stepList.add(new Step(4, description, state));
                    }
                    //else update distance of v, set u to new parent
                    else {
                        // take old edge of v and make useless
                        Edge uselessEdge = graph.getEdge(parent.get(v).getId(), v.getId());
                        uselessEdges.add(uselessEdge);
                        // update path to v
                        //queue.add(v);
                        visited.put(v, true);
                        distance.put(v, distance.get(vertex)+tmpDis); // save distance of v to source
                        description = "d["+v.getId()+"] is > d["+vertex.getId()+"] + w[u,v]\n" + "=>Push " + v.getId() + " to queue.\n" + "Set parent["+v.getId()+"] = " + vertex.getId() + ", d[" + v.getId() + "] = " + distance.get(v);
                        edgesHighlighted.remove(currentEdge);
                        vertexQueued.add(v);
                        //     verticesTraversed.add(v);
                        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
                        stepList.add(new Step(4, description, state));
                    }
                }
            }
            verticesHighlighted.remove(vertex);
        }
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed, vertexQueued, uselessEdges, distance);
        stepList.add(new Step(5, "End of Dijkstra", state));
    }

}


