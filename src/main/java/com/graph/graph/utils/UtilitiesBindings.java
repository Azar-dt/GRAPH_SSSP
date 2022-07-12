package com.graph.graph.utils;

import javafx.beans.binding.DoubleBinding;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import javafx.beans.value.ObservableDoubleValue;

public class UtilitiesBindings {

    public static DoubleBinding atan2(final ObservableDoubleValue y, final ObservableDoubleValue x) {
        return createDoubleBinding(() -> Math.atan2(y.get(), x.get()), y, x);
    }

    public static DoubleBinding toDegrees(final ObservableDoubleValue angrad) {
        return createDoubleBinding(() -> Math.toDegrees(angrad.get()), angrad);
    }
}
