module com.graph.graph {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires java.logging;

    exports com.graph.graph;
    exports com.graph.graph.graphcore;
    opens com.graph.graph.graphcore to javafx.fxml;
    opens com.graph.graph.controllers to javafx.fxml;
}