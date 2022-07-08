package com.graph.graph.containers;

import com.graph.graph.graphview.GraphPanel;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class SmartGraphDemoContainer extends BorderPane {

    public SmartGraphDemoContainer(GraphPanel graphView) {

        setCenter(new ContentZoomPane(graphView));

        //create bottom pane with controls
        HBox bottom = new HBox(10);

        CheckBox automatic = new CheckBox("Automatic layout");
        automatic.selectedProperty().bindBidirectional(graphView.automaticLayoutProperty());

        bottom.getChildren().add(automatic);

        setBottom(bottom);
    }


}