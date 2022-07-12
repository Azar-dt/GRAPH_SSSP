package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;
import com.graph.graph.utils.PressEnterToContinue;

import java.util.*;

public class BellmanFord extends Algorithm {


    public BellmanFord() {
        super();
        // init pseudoStep
        pseudoStep.put(0, "initSSSP");
        pseudoStep.put(1, "for i = 1 to |V|-1");
        pseudoStep.put(2, " for each edge(u, v) in E // in Edge List order\n");
        pseudoStep.put(3, "relax(u, v, w(u, v))");

        pseudoStep.put(4, "for each edge(u, v) in E\n" +
                "  if can still relax that edge, -∞ cycle found");
        pseudoStep.put(5, "End of Bellman Ford");
    }

    @Override
    public void run() {
        stepList.clear(); // clear stepList everytime start run algorithm
        Scanner sc = new Scanner(System.in);
        System.out.println("-------------Bellman Ford-------------");
        Queue<Vertex> queue = new LinkedList<>();
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        HashMap<Vertex, Vertex> parent = new HashMap<>();
        HashMap<Vertex, Integer> distance = new HashMap<>();
        Integer currentDistance = 0;

        //for save step
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

        //for algorithm
        queue.add(startVertex);
        visited.put(startVertex, true);
        distance.put(startVertex, currentDistance);

        //step 0
        vertexQueued.add(startVertex);
        description = startVertex.getId() + " is source vertex.\n"
                + "Set parent[v] = - 1, d[ " + startVertex.getId() + " ] = 0 and push this vertex to queue.";
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                verticesTraversed, edgesTraversed, vertexQueued, uselessEdges);

        stepList.add(new Step(0, description, state));

        //core algorithm
        Set<Vertex> vertexSet = graph.getVertices();
        Set<Edge> edgeSet = graph.getEdges();
        // Step 1: Initialize distances from src to all other
        // vertices as INFINITE
        for (Vertex vertex :
                vertexSet) {
            distance.put(vertex, Integer.MAX_VALUE);
        }
        distance.put(startVertex, currentDistance);
        queue.add(startVertex);
        visited.put(startVertex, true);

        /*
        // Step 2: Relax all edges |V| - 1 times. A simple
        // shortest path from src to any other vertex can
        // have at-most |V| - 1 edges
         for (int i = 1; i < V; ++i) {
            for (int j = 0; j < E; ++j) {
                int u = graph.edge[j].src;
                int v = graph.edge[j].dest;
                int weight = graph.edge[j].weight;
                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v])
                    dist[v] = dist[u] + weight;
            }
        }
         */

        int count = 1;
        int edge_processed = 1;
        State preState = null;
        boolean isChanged = false;
        for (int i = 1; i < vertexSet.size() ; ++i) {


            if (count == 1) {
                verticesTraversed.addAll(vertexSet);
                uselessEdges.addAll(edgeSet);

                description = "This is the first pass.\\n" +
                        "The highlighted edges are the current SSSP spanning tree so far.";
                stepList.add(new Step(1, description, new State(vertexList, edgeList,
                        verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed,
                        vertexQueued, uselessEdges)));
                verticesTraversed.clear();
                uselessEdges.clear();
                vertexQueued.clear();
            } else {
                if (isChanged) {
                    description = edgesTraversed.size() + " orange edge relaxation(s) in the last pass, we will continue.\n" +
                            "\nThe highlighted edges are the current SSSP spanning tree so far.";
                    verticesTraversed.addAll(vertexSet);

                }
                else {
                    description =  "There is no change in the last pass, we can stop Bellman-Ford now." +
                            "\nThe highlighted edges are the current SSSP spanning tree so far.";
                }
                stepList.add(new Step(1, description, preState));
            }
            verticesTraversed.clear();
            verticesHighlighted.clear();
            uselessEdges.clear();
            edgesHighlighted.clear();
            edgesTraversed.clear();
            stepList.add(new Step(1, "Prepare all edges for this #pass: " + count,
                    new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                            verticesTraversed, edgesTraversed, vertexQueued, uselessEdges)));
            isChanged = false;
            for (Edge edge :
                    edgeSet) {
                Vertex u = edge.getSource();
                Vertex v = edge.getDestination();
                int weight = edge.getWeight();
                verticesHighlighted.add(u);
                verticesHighlighted.add(v);
                edgesHighlighted.add(edge);
                description = "#Pass:" + count + ", " +
                        "relax(" + u.getId() + "," + v.getId() + "," +
                        weight + "),#edge_processed = " + edge_processed;
                stepList.add(new Step(2, description,
                        new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                                verticesTraversed, edgesTraversed, vertexQueued, uselessEdges)));
                verticesHighlighted.clear();
                edgesHighlighted.clear();
                vertexQueued.add(u);
                vertexQueued.add(v);
                if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + weight < distance.get(v)) {
                    distance.put(v, distance.get(u) + weight);
                    uselessEdges.remove(edge);
                    edgesTraversed.add(edge);
                    parent.put(v, u);
                    stepList.add(new Step(3, "#Pass:" + count + ", " +
                            "relax(" + u.getId() + "," + v.getId() + "," +
                            weight + "),#edge_processed = " + edge_processed
                            + "\nd[" +v.getId() +"] = " + distance.get(v)
                            + ", " + "p[" + v.getId() + "] = " + parent.get(v).getId(),
                            new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                                    verticesTraversed, edgesTraversed, vertexQueued, uselessEdges)));
                    isChanged = true;
                } else {
                    if (!edgesTraversed.contains(edge))
                    uselessEdges.add(edge);
                    stepList.add(new Step(3, "#Pass:" + count + ", " +
                            "relax(" + u.getId() + "," + v.getId() + "," +
                            weight + "),#edge_processed = " + edge_processed + "\nNo Change.",
                            new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                                    verticesTraversed, edgesTraversed, vertexQueued, uselessEdges)));
                }

                edge_processed++;
            }
            if (isChanged) {
                verticesTraversed.addAll(vertexSet);
                preState = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted,
                        verticesTraversed, edgesTraversed, vertexQueued, uselessEdges);
            }
            count++;
        }
        /*
        // Step 3: check for negative-weight cycles. The above
        // step guarantees shortest distances if graph doesn't
        // contain negative weight cycle. If we get a shorter
        // path, then there is a cycle.
        for (int j = 0; j < E; ++j) {
            int u = graph.edge[j].src;
            int v = graph.edge[j].dest;
            int weight = graph.edge[j].weight;
            if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                System.out.println("Graph contains negative weight cycle");
                return;
            }
        }
        printArr(dist, V);
         */
        for (Edge edge :
                edgeSet) {
            Vertex u = edge.getSource();
            Vertex v = edge.getDestination();
            int weight = edge.getWeight();
            if (distance.get(u) != Integer.MAX_VALUE && distance.get(u) + weight < distance.get(v)) {
//                stepList.add(new Step(3, "Graph contains negative weight cycle"));
                return;

            }

        }
//        stepList.add(new Step(3, "#edge_processed = " + edge_processed + ", V*E = " +
//                vertexSet.size() + "*" + edgeSet.size() + " = " + vertexSet.size() + edgeSet.size() + ".\n" +
//                "This is the SSSP spanning tree from source vertex 0."));
        ArrList(distance, vertexSet);

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

    public void ArrList(HashMap<Vertex, Integer> distance, Set<Vertex> vertexSet) {
        System.out.println("Vertex Distance from Source");
        for (Vertex v :
                vertexSet) {
            if (distance.get(v) != Integer.MAX_VALUE)
                System.out.println(v.getId() + "\t\t" + distance.get(v));
            else
                System.out.println(v.getId() + "\t\t∞");

        }

    }
}
