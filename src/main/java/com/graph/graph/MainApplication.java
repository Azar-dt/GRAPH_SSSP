package com.graph.graph;

import com.graph.graph.graphcore.Graph;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage ignored) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("JavaFX SmartGraph Visualization");
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Graph build_sample_digraph() {

        Graph g = new Graph();

        g.addVertex("A");
        g.addVertex("B");
        g.addVertex("C");
        g.addVertex("D");
        g.addVertex("E");
        g.addVertex("F");

        g.addEdge("A", "B");
        g.addEdge("B", "A");
        g.addEdge("A", "C");
        g.addEdge("A", "D");
        g.addEdge("B", "C");
        g.addEdge("C", "D");
        g.addEdge("B", "E");
        g.addEdge("F", "D");
        g.addEdge("F", "D");

        //yep, its a loop!
        g.addEdge("A", "A");

        return g;
    }
}
