package com.graph.graph.controllers;

import com.graph.graph.algorithm.Algorithm;
import com.graph.graph.algorithm.BFS;
import com.graph.graph.algorithm.BellmanFord;
import com.graph.graph.algorithm.Dijkstra;
import com.graph.graph.containers.SmartGraphDemoContainer;
import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphview.GraphPanel;
import com.graph.graph.graphview.GraphVertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    private final int NO_ITERATION = -1;
    private final int ANIMATION_PLAY = 1;
    private final int ANIMATION_PAUSE = 0;
    private final int ANIMATION_STOP = -1;
    private final int DEFAULT_DURATION = 1000;
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

    List<Step> stepList = new ArrayList<>();

    private SequentialTransition sequentialTransition;

    private int currentIteration = NO_ITERATION, maxIteration = 0, animationStatus = ANIMATION_STOP;
    private boolean isPlaying = false, isPaused = false;
    private int animationDuration = DEFAULT_DURATION;
    private PauseTransition pauseControl = null;

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
        algorithm.setGraph(graph);
    }

    public void setOnChooseDijkstra() {
        algo.setText("Dijkstra");
        algorithm = new Dijkstra();
        algorithm.setGraph(graph);
    }

    public void setOnChooseBellmanFord() {
        algo.setText("Bellman Ford");
        algorithm = new BellmanFord();
        algorithm.setGraph(graph);
    }

    public void onSelectStartVertex() {
        if (algorithm == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No algorithm selected");
            alert.setContentText("Please select an algorithm first");
            alert.showAndWait();
            return;
        }
        String startVertexId = startVertex.getText();
        if (startVertexId.isEmpty()) {
            return;
        }
        algorithm.setStartVertex(graph.getVertex(startVertexId));
        graphPanel.setDefaultState();
    }

    public void onResetGraph() {
        graph = new Graph();
        graphPanel = new GraphPanel(graph);
        bindingConsumer();
//        graphPanel.setState(stepList.get(0).getState());
        graphView.setCenter(new SmartGraphDemoContainer(graphPanel));
    }

    public void onPlayBtn() {
        if (algorithm == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ALgorithm not selected");
            alert.setHeaderText("Please select an algorithm");
            alert.showAndWait();
            return;
        }
        runAlgorithm();
    }

    public void runAlgorithm() {
        algorithm.run();
        stepList = algorithm.getStepList();
        startAnimation();
    }

    public void startAnimation() {
        maxIteration = stepList.size();
        if (currentIteration == NO_ITERATION)
            currentIteration = 0;
        isPlaying = true;
        isPaused = false;
        play();
    }

    private synchronized void play() {
        if (isPlaying) {
            isPaused = false;
            if (currentIteration < 0)
                currentIteration = 0;
//            playBtn.setVisible(false);
//            replayBtn.setVisible(false);
//            pauseBtn.setVisible(true);

            sequentialTransition = new SequentialTransition();
            if (animationStatus == ANIMATION_STOP) {
                animationStatus = ANIMATION_PLAY;
                updateDisplay(stepList.get(currentIteration));
            } else {
                animationStatus = ANIMATION_PLAY;
                animate();
            }
            int i = currentIteration;
            for (; i < maxIteration; i++) {
                PauseTransition pause = new PauseTransition(Duration.millis(animationDuration));
                pause.setOnFinished(e -> {
                    animate();
                });
                if (animationStatus == ANIMATION_STOP || animationStatus == ANIMATION_PAUSE)
                    return;
                sequentialTransition.getChildren().add(pause);
            }

            sequentialTransition.playFromStart();
        }
    }

    private synchronized void animate() {
        if (currentIteration >= maxIteration && animationStatus != ANIMATION_STOP)
            animationStatus = ANIMATION_PAUSE;
        if (animationStatus == ANIMATION_STOP || animationStatus == ANIMATION_PAUSE)
            return;

        next();
        /*
         * PauseTransition pause = new
         * PauseTransition(Duration.millis(animationDuration));
         * pause.setOnFinished(e -> {
         * animate();
         * });
         * sequentialTransition.getChildren().add(pause);
         */
    }

    private void next() {
        if (currentIteration < 0)
            currentIteration = 0;
        currentIteration++;
        if (currentIteration >= maxIteration) {
            currentIteration = maxIteration - 1;
            animationStatus = ANIMATION_PAUSE;
            isPaused = true;
            return;
        }
//        progressSlider.setValue(currentIteration);
        updateDisplay(stepList.get(currentIteration));
    }

    public void stepForward() {
        if (isPlaying) {
            if (!isPaused) {
                sequentialTransition.stop();
                next();
                if (pauseControl == null) {
                    pauseControl = new PauseTransition(Duration.millis(animationDuration));
                    pauseControl.setOnFinished(e -> {
                        play();
                        pauseControl = null;
                    });
                    pauseControl.play();
                }
            } else {
                next();
            }
        }
    }

    public void updateDisplay(Step step) {
        updateGraphDisplay(step.getState());
//        updateDetailCodeDisplay(step.getStatus());
//        highlightPseudoCode(step.getLineNo());
    }

    private void updateGraphDisplay(State state) {
        graphPanel.setState(state);
    }
}
