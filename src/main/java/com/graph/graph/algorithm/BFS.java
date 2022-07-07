package com.graph.graph.algorithm;

import com.graph.graph.graphcore.Vertex;
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

        stepList.add(new Step(0, "0 is the source vertex.\n" + "Set parent[v] = -1, d[" + startVertex.getId() + "] = 0 and push this vertex to queue."));
        queue.add(startVertex);
        visited.put(startVertex, true);
        distance.put(startVertex, currentDistance);

        // core algorithm
        while (queue.size() > 0) {
            currentDistance++;
            StringBuilder sb = new StringBuilder();
            for (Vertex v : queue) {
                sb.append(v.getId() + " ");
            }
            stepList.add(new Step(1, "The queue is : { " + sb.toString() + "}"));

            Vertex vertex = queue.remove();
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
                stepList.add(new Step(2, step2.toString()));
                if (!visited.containsKey(v)) {
                    parent.put(v, vertex); // save the parent of v
                    queue.add(v);
                    visited.put(v, true);
                    distance.put(v, currentDistance); // save distance of v to source
                    stepList.add(new Step(3, "Push " + v.getId() + " to queue.\n" + "Set parent[v] = " + vertex.getId() + ", d[" + v.getId() + "] = " + currentDistance));
                }

            }
        }
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

