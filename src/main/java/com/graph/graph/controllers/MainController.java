package com.graph.graph.controllers;

import com.graph.graph.algorithm.Algorithm;
import com.graph.graph.algorithm.BFS;
import com.graph.graph.algorithm.BellmanFord;
import com.graph.graph.algorithm.Dijkstra;
import com.graph.graph.containers.SmartGraphDemoContainer;
import com.graph.graph.graphcore.Graph;
import com.graph.graph.graphcore.Vertex;
import com.graph.graph.graphview.GraphPanel;
import com.graph.graph.graphview.GraphVertex;
import com.graph.graph.step.State;
import com.graph.graph.step.Step;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
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
    @FXML
    public Button playBtn;
    @FXML
    public Button pauseBtn;
    @FXML
    public Label detailShow;
    @FXML
    public MenuButton exampleGraph;
    @FXML
    public MenuItem graph1;
    @FXML
    public MenuItem graph2;
    @FXML
    public MenuItem graph3;
    @FXML
    public MenuItem graph4;
    @FXML
    public Slider progressSlider;
    @FXML
    public Rectangle progressRec;
    @FXML
    public Slider speedControl;
    @FXML
    public Label speedLabel;


    private GraphPanel graphPanel;
    private Graph graph = new Graph();

    private Algorithm algorithm;

    List<Step> stepList = new ArrayList<>();

    private HashMap<Integer, String> pseudoStepMap;
    private HashMap<Integer, Label> pseudoStepLabelMap;
    private SequentialTransition sequentialTransition;

    private int currentIteration = NO_ITERATION, maxIteration = 0, animationStatus = ANIMATION_STOP;
    private boolean isPlaying = false, isPaused = false;
    private int animationDuration = DEFAULT_DURATION;
    private PauseTransition pauseControl = null;

    @FXML
    private void initialize() {
        initGraphPanel();
        Platform.runLater(() -> {
            graphPanel.init(); //init graph after scene is created
        });
        setupSpeedControl();
        setupProgressShow();
        for (MenuItem item : algo.getItems()) {
            item.setOnAction((ActionEvent event) -> {
                if (item.getText().equals("BFS")) {
                    algorithm = new BFS();
                    algo.setText("BFS");
                } else if (item.getText().equals("Dijkstra")) {
                    algorithm = new Dijkstra();
                    algo.setText("Dijkstra");
                } else if (item.getText().equals("Bellman-Ford")) {
                    algorithm = new BellmanFord();
                    algo.setText("Bellman-Ford");
                }
                resetControl();
                initPseudoStep();
            });
        }

        for (MenuItem item : exampleGraph.getItems()) {
            item.setOnAction((ActionEvent event) -> {
                if (item.getText().equals(graph1.getText())) {
                    graph = Graph.createGraphCP443DU();
                    initGraphPanel();
                }
                if (item.getText().equals(graph2.getText())) {
                    graph = Graph.createGraphCP410DW();
                    initGraphPanel();
                }
                if (item.getText().equals(graph3.getText())) {
                    graph = Graph.createGraphCP416DWDAG();
                    initGraphPanel();
                }
                if (item.getText().equals(graph4.getText())) {
                    graph = Graph.createGraphBIG();
                    initGraphPanel();
                }
            });
        }
    }

    private void initGraphPanel() {
        graphPanel = new GraphPanel(graph);
        graphView.setCenter(new SmartGraphDemoContainer(graphPanel));
        bindingConsumer();
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
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Set Vertex Label");
            dialog.setHeaderText("Input Vertex Label");
            dialog.setContentText("Please input vertex label:");
            dialog.showAndWait().ifPresent(label -> {
                Vertex temp = graph.getVertex(label);
                if (temp != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Duplicate Vertex");
                    alert.setContentText("Vertex " + label + " already exists");
                    alert.showAndWait();
                } else {
                    graph.getVertex(graphVertex.getUnderlyingVertex()).setId(label);
                }
            });
        });

        graphPanel.setEdgeDoubleClickAction(graphEdge -> {
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Set Edge Weight");
            dialog.setHeaderText("Input Edge Weight");
            dialog.setContentText("Please input edge Weight:");
            dialog.showAndWait().ifPresent(label -> {
                double newWeight;
                if (label.equals("infinity")) {
                    graph.getEdge(graphEdge.getUnderlyingEdge()).setWeight(Double.MAX_VALUE);
                } else {
                    try {
                        newWeight = Double.parseDouble(label);
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Input");
                        alert.setContentText("Please input a valid number");
                        alert.showAndWait();
                        newWeight = graphEdge.getUnderlyingEdge().getWeight();
                    }
                    graph.getEdge(graphEdge.getUnderlyingEdge()).setWeight(newWeight);
                }
            });
        });
    }

    private void setupSpeedControl() {
        StringProperty speedLabelProperty = new SimpleStringProperty("");
        speedLabel.textProperty().bind(speedLabelProperty.concat(speedControl.valueProperty().asString()).concat("x"));

        speedControl.valueProperty().addListener((ov, oldValue, newValue) -> {
            double doubleValue = newValue.doubleValue();
            if (doubleValue >= 0.5 && doubleValue < 0.625) {
                doubleValue = 0.5;
            } else if (doubleValue >= 0.625 && doubleValue < 0.875) {
                doubleValue = 0.75;
            } else if (doubleValue >= 0.875 && doubleValue < 1.125) {
                doubleValue = 1;
            } else if (doubleValue >= 1.125 && doubleValue < 1.375) {
                doubleValue = 1.25;
            } else
                doubleValue = 1.5;

//            doubleValue = boundValue(doubleValue, speedControl.getMin(), speedControl.getMax());
            speedControl.setValue(doubleValue);
            animationDuration = (int) (DEFAULT_DURATION / speedControl.getValue());
            if (isPlaying && !isPaused) {
                pause();
                if (pauseControl == null) {
                    pauseControl = new PauseTransition(Duration.millis(animationDuration));
                    pauseControl.setOnFinished(ev -> {
                        play();
                        pauseControl = null;
                    });
                    pauseControl.play();
                }
            }
        });
    }

    private void setupProgressShow() {
        progressSlider.setPrefWidth(200);
        progressSlider.setBlockIncrement(1);
//        progressSlider.maxProperty().bind();
//        progressRec.heightProperty().bind(progressSlider.heightProperty().subtract(2));
//        progressRec.widthProperty().bind(progressSlider.widthProperty());

        progressSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
            int value = Math.round(newValue.floatValue());
            int percent = maxIteration == 0 ? 0 : Math.round((value * 100) / (maxIteration - 1));
//            String style = String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #ccc %d%%);", percent, percent);
//            progressRec.setStyle(style);
            String style = String.format("-fx-background-color: linear-gradient(to right, #2D819D %d%%, #969696 %d%%);", newValue.intValue(), newValue.intValue());
            progressSlider.setValue(value);
        });

        progressSlider.setOnMousePressed(e -> {
            if (isPlaying && e.isPrimaryButtonDown()) {
                if (!isPaused) {
                    sequentialTransition.stop();
                    jumpToIteration((int) (progressSlider.getValue()));
                    if (pauseControl == null) {
                        pauseControl = new PauseTransition(Duration.millis(animationDuration));
                        pauseControl.setOnFinished(ev -> {
                            play();
                            pauseControl = null;
                        });
                        pauseControl.play();
                    }
                } else {
                    jumpToIteration((int) (progressSlider.getValue()));
                }
            }
        });

        progressSlider.setOnMouseDragged(e -> {
            if (isPlaying && e.isPrimaryButtonDown()) {
                pause();
                jumpToIteration((int) (progressSlider.getValue()));
            }
        });

        progressSlider.setOnMouseDragExited(e -> {
            if (isPlaying && e.isPrimaryButtonDown()) {
                if (!isPaused) {
                    sequentialTransition.stop();
                    jumpToIteration((int) (progressSlider.getValue()));
                    if (pauseControl == null) {
                        pauseControl = new PauseTransition(Duration.millis(animationDuration));
                        pauseControl.setOnFinished(ev -> {
                            play();
                            pauseControl = null;
                        });
                        pauseControl.play();
                    }
                } else {
                    jumpToIteration((int) (progressSlider.getValue()));
                }
            }
        });
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
        algorithm.setGraph(graph);
        algorithm.setStartVertex(graph.getVertex(startVertexId));
        graphPanel.setDefaultState();
    }

    public void onResetGraph() {
        pause();
        algorithm = null; //reset algorithm
        algo.setText("Choose Algorithm");
        startVertex.clear();

        resetControl();
//        graphPanel.setState(stepList.get(0).getState());
        graph = new Graph();
        graphPanel = new GraphPanel(graph);
        bindingConsumer();
        graphView.setCenter(new SmartGraphDemoContainer(graphPanel));
    }

    private void resetControl() {
        detailShow.setText("");
        pseudoStep.getChildren().clear();

        isPlaying = false;
        isPaused = false;
        currentIteration = NO_ITERATION;
        progressSlider.setMax(0);
        progressSlider.setValue(0);
    }

    public void onPlayBtn() {
        if (algorithm == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ALgorithm not selected");
            alert.setHeaderText("Please select an algorithm");
            alert.showAndWait();
            return;
        }
        Vertex temp = graph.getVertex(startVertex.getText());
        if (temp == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Start vertex not found");
            alert.setHeaderText("Please select a valid start vertex");
            alert.showAndWait();
            return;
        }
        runAlgorithm();
    }

    public void onPauseBtn() {
        if (isPlaying) {
            pause();
        }
    }

    public void onNextBtn() {
        stepForward();
    }

    public void onBackBtn() {
        stepBackward();
    }

    public void onGoToBegin() {
        goToBegin();
    }

    public void onGoToEnd() {
        goToEnd();
    }

    private void initPseudoStep() {
        pseudoStepMap = algorithm.getPseudoStep();
        pseudoStep.getChildren().clear();
        pseudoStepLabelMap = new HashMap<>();
        for (int i = 0; i < pseudoStepMap.size(); i++) {
            Label label = new Label(pseudoStepMap.get(i));
            pseudoStepLabelMap.put(i, label);
            pseudoStep.getChildren().add(label);
        }
    }

    public void runAlgorithm() {
        algorithm.run();
        stepList = algorithm.getStepList();
        startAnimation();
    }

    public void startAnimation() {
        maxIteration = stepList.size();
        progressSlider.setMax(maxIteration - 1);
        if (currentIteration == NO_ITERATION)
            currentIteration = 0;
        isPlaying = true;
        isPaused = false;
        play();
    }

    private void pause() {
        isPaused = true;
        animationStatus = ANIMATION_PAUSE;
        if (sequentialTransition != null) {
            sequentialTransition.stop();
        }
//        sequentialTransition.stop();
        pauseBtn.setVisible(false);
        playBtn.setVisible(true);
    }

    private synchronized void play() {
        if (isPlaying) {
            isPaused = false;
            if (currentIteration < 0)
                currentIteration = 0;
            playBtn.setVisible(false);
//            replayBtn.setVisible(false);
            pauseBtn.setVisible(true);

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
        progressSlider.setValue(currentIteration);
        updateDisplay(stepList.get(currentIteration));
    }

    private void previous() {
        if (currentIteration >= maxIteration)
            currentIteration = maxIteration - 1;
        currentIteration--;
        if (currentIteration < 0)
            return;
        updateDisplay(stepList.get(currentIteration));
    }

    private void stepBackward() {
        if (isPlaying) {
            if (!isPaused) {
                sequentialTransition.stop();
                previous();
                if (pauseControl == null) {
                    pauseControl = new PauseTransition(Duration.millis(animationDuration));
                    pauseControl.setOnFinished(e -> {
                        play();
                        pauseControl = null;
                    });
                    pauseControl.play();
                }
            } else {
                previous();
            }
        }
    }

    private void stepForward() {
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

    private void jumpToIteration(int iteration) {
        if (isPaused == false)
            sequentialTransition.stop();
        currentIteration = iteration;
        if (currentIteration >= maxIteration) {
            currentIteration = maxIteration - 1;
        }
        if (currentIteration < 0)
            currentIteration = 0;
        progressSlider.setValue(currentIteration);
        updateDisplay(stepList.get(currentIteration));
    }

    private void goToBegin() {
        pause();
        jumpToIteration(0);
    }

    private void goToEnd() {
        pause();
        jumpToIteration(maxIteration - 1);
    }


    public void updateDisplay(Step step) {
        updateGraphDisplay(step.getState());
        updateDetailCodeDisplay(step.getDescription());
        highlightPseudoCode(step.getId());
    }

    private void updateGraphDisplay(State state) {
        graphPanel.setState(state);
    }

    private void updateDetailCodeDisplay(String detail) {
        detailShow.setText(detail);
    }

    public void highlightPseudoCode(int lineNo) {
        resetPseudoCodeCss();
        pseudoStepLabelMap.get(lineNo).setStyle("-fx-font-size: 14;-fx-word-wrap: break-word;-fx-font-weight: bold; -fx-background-color: #333; -fx-text-fill: #eee;");

    }

    private void resetPseudoCodeCss() {
        for (Label label : pseudoStepLabelMap.values()) {
            label.setStyle("-fx-font-size: 14;-fx-word-wrap: break-word;");
        }
    }
}
