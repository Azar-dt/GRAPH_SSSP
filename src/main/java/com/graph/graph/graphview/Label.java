package com.graph.graph.graphview;

import javafx.scene.text.Text;

public class Label extends Text implements StylableNode {

    private final StyleProxy styleProxy;

    public Label() {
        this(0, 0, "");
    }

    public Label(String text) {
        this(0, 0, text);
    }

    public Label(double x, double y, String text) {
        super(x, y, text);
        styleProxy = new StyleProxy(this);
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

}

