package com.graph.graph.controllers;

import com.graph.graph.algorithm.Algorithm;
import com.graph.graph.algorithm.BFS;
import com.graph.graph.algorithm.BellmanFord;
import com.graph.graph.algorithm.Dijkstra;
import com.graph.graph.containers.SmartGraphDemoContainer;
import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphview.GraphPanel;
import com.graph.graph.graphview.GraphVertex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    public MenuItem bfs;
    @FXML
    public MenuItem dijkstra;
    @FXML
    public MenuItem bellmanFord;
    @FXML
    public TextField startVertex;
    @FXML
    public Button reset;
    @FXML
    public BorderPane graphView;
    @FXML
    public Pane detailStep;
    @FXML
    public VBox pseudoStep;
    @FXML
    public MenuButton algo;

    private GraphPanel graphPanel;
    private Graph graph = new Graph();

    private Algorithm algorithm;

    @FXML
    private void initialize() {
        graph.addVertex("A", 100, 100);
        graphPanel = new GraphPanel(graph);
        graphView.setCenter(new SmartGraphDemoContainer(graphPanel));
//        graphPanel.setStyle("-fx-background-color: #000;");
        bindingConsumer();
        Platform.runLater(() -> {
            graphPanel.init(); //init graph after scene is created
        });

    }

    private void bindingConsumer() {
        List<GraphVertex> pickedVertices = new ArrayList<>(2);
        graphPanel.setVertexAltClickAction((GraphVertex graphVertex) -> {
            System.out.println("blalala");
            pickedVertices.add(graphVertex);
            graphVertex.addStyleClass("pickedVertex");
            if (pickedVertices.size() == 2) {
                graph.addEdge(pickedVertices.get(0).getUnderlyingVertex().getId(), pickedVertices.get(1).getUnderlyingVertex().getId());
                for (GraphVertex v : pickedVertices) {
                    v.removeStyleClass("pickedVertex");
                }
                pickedVertices.clear();
            }
        });

        graphPanel.setVertexDoubleClickAction((GraphVertex graphVertex) -> {
            System.out.println("Vertex contains element: " + graphVertex.getUnderlyingVertex().getId());

            graphVertex.addStyleClass("myVertex");
            //want fun? uncomment below with automatic layout
//            g.removeVertex(graphVertex.getUnderlyingVertex().getId());
//            graphPanel.update();
        });

        graphPanel.setEdgeDoubleClickAction(graphEdge -> {
            System.out.println("Edge contains element: " + graphEdge.getUnderlyingEdge().getWeight());
            //dynamically change the style when clicked
            graphEdge.setStyle("-fx-stroke: black; -fx-stroke-width: 3;");

            graphEdge.getStylableArrow().setStyle("-fx-stroke: black; -fx-stroke-width: 3;");

        });
    }

    public void setOnChooseBFS() {
        algo.setText("BFS");
        algorithm = new BFS();
    }

    public void setOnChooseDijkstra() {
        algo.setText("Dijkstra");
        algorithm = new Dijkstra();
    }

    public void setOnChooseBellmanFord() {
        algo.setText("Bellman Ford");
        algorithm = new BellmanFord();
    }

    public void onResetGraph() {
        graph = new Graph();
        GraphPanel graphPanel = new GraphPanel(graph);
        graphView.setCenter(new SmartGraphDemoContainer(graphPanel));
        bindingConsumer();
    }
}
