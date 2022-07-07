package com.graph.graph.graphcore;

import java.util.*;

public class Graph {
    private Set<Vertex> vertices = new TreeSet<Vertex>();

    private Set<Edge> edges = new TreeSet<Edge>();

    public int numVertices() {
        return vertices.size();
    }

    public int numEdges() {
        return edges.size();
    }

    public void addVertex(String id) {
        // TODO
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                System.out.println("Vertex already exists");
                return;
            }
        }
        vertices.add(new Vertex(id));
        System.out.println("Vertex added");
    }

    public void addVertex(String id, double px, double py) {
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                throw new InvalidVertexException("Vertex already exist");
            }
        }
        vertices.add(new Vertex(id, px, py));
    }

    public void addVertex(Vertex vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }

    public void removeVertex(Vertex vertex) {
        for (Vertex v : vertices) {
            if (v.compareTo(vertex) == 0) {
                vertices.remove(v);
                edges.removeIf(e -> e.getSource().compareTo(v) == 0 || e.getDestination().compareTo(v) == 0);
                return;
            }
        }
    }

    public void removeVertex(String id) {
        boolean isExist = false;
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                isExist = true;
                vertices.remove(v);
                System.out.println("Vertex removed");
                return;
            }
        }
        if (isExist) {
            edges.removeIf(edge -> edge.getSource().getId().equals(id) || edge.getDestination().getId().equals(id));
        } else {
            System.out.println("Vertex does not exist");
        }
    }

    public void addEdge(String vertex1Id, String vertex2Id) {
        // add edge to the graph
        Vertex vertex1 = null;
        Vertex vertex2 = null;
        // find vertex1 and vertex2 if they exist
        for (Vertex v : vertices) {
            if (v.getId().equals(vertex1Id)) {
                vertex1 = v;
            }
            if (v.getId().equals(vertex2Id)) {
                vertex2 = v;
            }
        }
        if (vertex1 == null || vertex2 == null) {
            System.out.println("Vertex does not exist");
            return;
        } else {
            edges.add(new Edge(vertex1, vertex2, 1));
        }
        System.out.println("Edge added");
    }

    public void changeEdgeWeight(String vertex1Id, String vertex2Id, int weight) {
        // check if edge exists
        Vertex vertex1 = null;
        Vertex vertex2 = null;
        for (Vertex v : vertices) {
            if (v.getId().equals(vertex1Id)) {
                vertex1 = v;
            }
            if (v.getId().equals(vertex2Id)) {
                vertex2 = v;
            }
        }
        if (vertex1 == null || vertex2 == null) {
            System.out.println("Vertex does not exist");
            return;
        } else {
            for (Edge e : edges) {
                if (e.getSource().getId().equals(vertex1Id) && e.getDestination().getId().equals(vertex2Id)) {
                    e.setWeight(weight);
                    System.out.println("Edge weight changed");
                    return;
                }
            }
        }
    }

    public void removeEdge(Edge edge) {
        for (Edge e : edges) {
            if (e.compareTo(edge) == 0) {
                edges.remove(e);
                return;
            }
        }
    }

    public void removeEdge(String vertex1Id, String vertex2Id) {
        // remove edge from the graph
        Vertex vertex1 = null;
        Vertex vertex2 = null;
        for (Vertex v : vertices) {
            if (v.getId().equals(vertex1Id)) {
                vertex1 = v;
            }
            if (v.getId().equals(vertex2Id)) {
                vertex2 = v;
            }
        }
        for (Edge e : edges) {
            if (e.getSource().getId().equals(vertex1Id) && e.getDestination().getId().equals(vertex2Id)) {
                edges.remove(e);
                return;
            }

        }
        System.out.println("Edge does not exist");
    }

    public Collection incidentEdges(Vertex vertex) {
        // return all incident edges of a vertex
        Collection<Edge> incidentEdges = new ArrayList<Edge>();
        for (Edge e : edges) {
            if (e.getSource().getId().equals(vertex.getId()) || e.getDestination().getId().equals(vertex.getId())) {
                incidentEdges.add(e);
            }
        }
        return incidentEdges;
    }

    public static Graph createGraphCP441() {
        Graph graph = new Graph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");
        graph.addVertex("5");
        graph.addVertex("6");
        graph.addVertex("7");
        graph.addVertex("8");
        graph.addEdge("0", "1");
        graph.addEdge("1", "2");

        graph.addEdge("1", "3");
        graph.addEdge("2", "3");
        graph.addEdge("3", "4");

        graph.addEdge("6", "7");
        graph.addEdge("6", "8");
        return graph;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : vertices) {
            sb.append("Vertices:\n");
            sb.append(v.getId() + "\n");
            for (Edge e : edges) {
                if (e.getSource().getId().equals(v.getId())) {
                    sb.append(e.getDestination().getId() + " " + e.getWeight() + "\n");
                }
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    public Vertex getVertex(String id) {
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    public Edge getEdge(String vertex1Id, String vertex2Id) {
        for (Edge e : edges) {
            if (e.getSource().getId().equals(vertex1Id) && e.getDestination().getId().equals(vertex2Id)) {
                return e;
            }
        }
        return null;
    }

    public Set<Vertex> getNeighbors(String vertexId) {
        Set<Vertex> adjacencyList = new TreeSet<Vertex>();
        for (Edge e : edges) {
            if (e.getSource().getId().equals(vertexId)) {
                adjacencyList.add(e.getDestination());
            }
        }
        return adjacencyList;
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Vertex opposite(Vertex vertex, Edge edge) {
        if (edge.getSource().getId().equals(vertex.getId())) {
            return edge.getDestination();
        } else {
            return edge.getSource();
        }
    }

    public Boolean checkVertex(String id) {
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public Boolean checkEdge(String vertex1Id, String vertex2Id) {
        for (Edge e : edges) {
            if (e.getSource().getId().equals(vertex1Id) && e.getDestination().getId().equals(vertex2Id)) {
                return true;
            }
        }
        return false;
    }
}

