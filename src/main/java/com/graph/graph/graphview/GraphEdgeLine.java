package com.graph.graph.graphview;

import com.graph.graph.graphcore.Edge;
import com.graph.graph.utils.UtilitiesBindings;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class GraphEdgeLine extends Line implements GraphEdgeBase {

    private final Edge underlyingEdge;

    private final GraphVertexNode inbound;
    private final GraphVertexNode outbound;

    private Label attachedLabel = null;
    private Arrow attachedArrow = null;

    /* Styling proxy */
    private final StyleProxy styleProxy;

    public GraphEdgeLine(Edge edge, GraphVertexNode inbound, GraphVertexNode outbound) {
        if (inbound == null || outbound == null) {
            throw new IllegalArgumentException("Cannot connect null vertices.");
        }

        this.inbound = inbound;
        this.outbound = outbound;

        this.underlyingEdge = edge;

        styleProxy = new StyleProxy(this);
        styleProxy.addStyleClass("edge");

        //bind start and end positions to vertices centers through properties
        this.startXProperty().bind(outbound.centerXProperty());
        this.startYProperty().bind(outbound.centerYProperty());
        this.endXProperty().bind(inbound.centerXProperty());
        this.endYProperty().bind(inbound.centerYProperty());

        setOnMouseEntered((MouseEvent event) -> {
            getScene().setCursor(Cursor.HAND);
        });
        setOnMouseExited((MouseEvent event) -> {
            getScene().setCursor(Cursor.DEFAULT);
        });
    }

    @Override
    public void setStyleClass(String cssClass) {
        styleProxy.setStyleClass(cssClass);
    }

    @Override
    public void addStyleClass(String cssClass) {
        styleProxy.addStyleClass(cssClass);
    }

    @Override
    public boolean removeStyleClass(String cssClass) {
        return styleProxy.removeStyleClass(cssClass);
    }


    @Override
    public void attachLabel(Label label) {
        this.attachedLabel = label;
        this.attachedLabel.addStyleClass("edge-label");
        this.attachedLabel.setUnderline(true);

        System.out.println(label);
        /* rotate label around itself based on this line's angle */
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(translateXProperty());
        rotation.pivotYProperty().bind(translateYProperty());
        rotation.angleProperty().bind(UtilitiesBindings.toDegrees(UtilitiesBindings.atan2(endYProperty().subtract(startYProperty()), endXProperty().subtract(startXProperty()))));
//        System.out.println(rotation.angleProperty().get());

        double angle = rotation.angleProperty().get();
        double x = Math.sin(angle) / Math.abs(Math.sin(angle));
        double y = Math.cos(angle) / Math.abs(Math.cos(angle));
//        label.translateXProperty().bind(startXProperty().add(endXProperty().subtract(startXProperty()).divide(2).add(x * 5)));
//        label.translateYProperty().bind(startYProperty().add(endYProperty().subtract(startYProperty()).divide(2).subtract(y * 5)));
        if (angle >= 0 && angle < 90) {
            label.translateXProperty().bind(startXProperty().add(endXProperty().subtract(startXProperty()).divide(2).add(8)));
            label.translateYProperty().bind(startYProperty().add(endYProperty().subtract(startYProperty()).divide(2).add(-8)));
        }
        if (angle >= 90 && angle < 180) {
            label.translateXProperty().bind(startXProperty().add(endXProperty().subtract(startXProperty()).divide(2).add(8)));
            label.translateYProperty().bind(startYProperty().add(endYProperty().subtract(startYProperty()).divide(2).add(8)));
        }
        if (angle <= 0 && angle > -90) {
            label.translateXProperty().bind(startXProperty().add(endXProperty().subtract(startXProperty()).divide(2).add(-8)));
            label.translateYProperty().bind(startYProperty().add(endYProperty().subtract(startYProperty()).divide(2).add(-8)));
        }
        if (angle <= -90 && angle > -180) {
            label.translateXProperty().bind(startXProperty().add(endXProperty().subtract(startXProperty()).divide(2).add(-8)));
            label.translateYProperty().bind(startYProperty().add(endYProperty().subtract(startYProperty()).divide(2).add(8)));
        }
//        label.setRotate(rotation.angleProperty().get());
        label.getTransforms().add(rotation);

    }

    @Override
    public Label getAttachedLabel() {
        return attachedLabel;
    }

    @Override
    public Edge getUnderlyingEdge() {
        return underlyingEdge;
    }


    @Override
    public void attachArrow(Arrow arrow) {
        this.attachedArrow = arrow;

        /* attach arrow to line's endpoint */
        arrow.translateXProperty().bind(endXProperty());
        arrow.translateYProperty().bind(endYProperty());

        /* rotate arrow around itself based on this line's angle */
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(translateXProperty());
        rotation.pivotYProperty().bind(translateYProperty());
        rotation.angleProperty().bind(UtilitiesBindings.toDegrees(UtilitiesBindings.atan2(endYProperty().subtract(startYProperty()), endXProperty().subtract(startXProperty()))));

        arrow.getTransforms().add(rotation);

        /* add translation transform to put the arrow touching the circle's bounds */
        Translate t = new Translate(-outbound.getRadius(), 0);
        arrow.getTransforms().add(t);

    }

    @Override
    public Arrow getAttachedArrow() {
        return this.attachedArrow;
    }

    @Override
    public StylableNode getStylableArrow() {
        return this.attachedArrow;
    }

    @Override
    public StylableNode getStylableLabel() {
        return this.attachedLabel;
    }

}
