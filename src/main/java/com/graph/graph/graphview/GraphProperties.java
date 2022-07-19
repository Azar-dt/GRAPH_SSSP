package com.graph.graph.graphview;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphProperties {

    private static final boolean DEFAULT_VERTEX_ALLOW_USER_MOVE = true;
    private static final String PROPERTY_VERTEX_ALLOW_USER_MOVE = "vertex.allow-user-move";

    private static final double DEFAULT_VERTEX_RADIUS = 15;
    private static final String PROPERTY_VERTEX_RADIUS = "vertex.radius";

    private static final boolean DEFAULT_VERTEX_USE_TOOLTIP = true;
    private static final String PROPERTY_VERTEX_USE_TOOLTIP = "vertex.tooltip";

    private static final boolean DEFAULT_VERTEX_USE_LABEL = true;
    private static final String PROPERTY_VERTEX_USE_LABEL = "vertex.label";

    private static final boolean DEFAULT_EDGE_USE_TOOLTIP = true;
    private static final String PROPERTY_EDGE_USE_TOOLTIP = "edge.tooltip";

    private static final boolean DEFAULT_EDGE_USE_LABEL = true;
    private static final String PROPERTY_EDGE_USE_LABEL = "edge.label";

    private static final boolean DEFAULT_EDGE_USE_ARROW = true;
    private static final String PROPERTY_EDGE_USE_ARROW = "edge.arrow";

    private static final int DEFAULT_ARROW_SIZE = 5;
    private static final String PROPERTY_ARROW_SIZE = "edge.arrowsize";

    private static final double DEFAULT_REPULSION_FORCE = 25000;
    private static final String PROPERTY_REPULSION_FORCE = "layout.repulsive-force";

    private static final double DEFAULT_ATTRACTION_FORCE = 30;
    private static final String PROPERTY_ATTRACTION_FORCE = "layout.attraction-force";

    private static final double DEFAULT_ATTRACTION_SCALE = 10;
    private static final String PROPERTY_ATTRACTION_SCALE = "layout.attraction-scale";

    private Properties properties;

    /**
     * Uses default properties file.
     */
    public GraphProperties() {
        properties = new Properties();

        try {
            properties.load(new FileInputStream(getClass().getResource("/application.properties").toExternalForm()));
        } catch (IOException ex) {
            String msg = "The default %s was not found. Using default values.";
            Logger.getLogger(GraphProperties.class.getName()).log(Level.WARNING, msg);
        }
    }

    /**
     * Reads properties from the desired input stream.
     *
     * @param inputStream input stream from where to read the properties
     */
    public GraphProperties(InputStream inputStream) {
        properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException ex) {
            String msg = "The file provided by the input stream does not exist. Using default values.";
            Logger.getLogger(GraphProperties.class.getName()).log(Level.WARNING, msg);
        }
    }

    public GraphProperties(String content) {
        properties = new Properties();
        try {
            InputStream targetStream = new ByteArrayInputStream(content.getBytes());
            properties.load(targetStream);
        } catch (IOException ex) {
            String msg = "The string contents could not be loaded. Using default values.";
            Logger.getLogger(GraphProperties.class.getName()).log(Level.WARNING, msg);
        }
    }

    /**
     * Returns a property that indicates whether a vertex can be moved freely
     * by the user.
     *
     * @return corresponding property value
     */
    public boolean getVertexAllowUserMove() {
        return getBooleanProperty(PROPERTY_VERTEX_ALLOW_USER_MOVE, DEFAULT_VERTEX_ALLOW_USER_MOVE);
    }

    /**
     * Returns a property that indicates the radius of each vertex.
     *
     * @return corresponding property value
     */
    public double getVertexRadius() {
        return getDoubleProperty(PROPERTY_VERTEX_RADIUS, DEFAULT_VERTEX_RADIUS);
    }

    /**
     * Returns a property that indicates the repulsion force to use in the
     * automatic force-based layout.
     *
     * @return corresponding property value
     */
    public double getRepulsionForce() {
        return getDoubleProperty(PROPERTY_REPULSION_FORCE, DEFAULT_REPULSION_FORCE);
    }

    /**
     * Returns a property that indicates the attraction force to use in the
     * automatic force-based layout.
     *
     * @return corresponding property value
     */
    public double getAttractionForce() {
        return getDoubleProperty(PROPERTY_ATTRACTION_FORCE, DEFAULT_ATTRACTION_FORCE);
    }

    /**
     * Returns a property that indicates the attraction scale to use in the
     * automatic force-based layout.
     *
     * @return corresponding property value
     */
    public double getAttractionScale() {
        return getDoubleProperty(PROPERTY_ATTRACTION_SCALE, DEFAULT_ATTRACTION_SCALE);
    }

    /**
     * Returns a property that indicates whether a vertex has a tooltip installed.
     *
     * @return corresponding property value
     */
    public boolean getUseVertexTooltip() {
        return getBooleanProperty(PROPERTY_VERTEX_USE_TOOLTIP, DEFAULT_VERTEX_USE_TOOLTIP);
    }

    /**
     * Returns a property that indicates whether a vertex has a @link SmartLabel
     * attached to it.
     *
     * @return corresponding property value
     */
    public boolean getUseVertexLabel() {
        return getBooleanProperty(PROPERTY_VERTEX_USE_LABEL, DEFAULT_VERTEX_USE_LABEL);
    }

    /**
     * Returns a property that indicates whether an edge has a tooltip installed.
     *
     * @return corresponding property value
     */
    public boolean getUseEdgeTooltip() {
        return getBooleanProperty(PROPERTY_EDGE_USE_TOOLTIP, DEFAULT_EDGE_USE_TOOLTIP);
    }

    /**
     * Returns a property that indicates whether an edge has a @link SmartLabel
     * attached to it.
     *
     * @return corresponding property value
     */
    public boolean getUseEdgeLabel() {
        return getBooleanProperty(PROPERTY_EDGE_USE_LABEL, DEFAULT_EDGE_USE_LABEL);
    }

    /**
     * Returns a property that indicates whether a @link SmartArrow should be
     * attached to an edge.
     *
     * @return corresponding property value
     */
    public boolean getUseEdgeArrow() {
        return getBooleanProperty(PROPERTY_EDGE_USE_ARROW, DEFAULT_EDGE_USE_ARROW);
    }

    /**
     * Returns a property that indicates the size of the @link SmartArrow.
     *
     * @return corresponding property value
     */
    public double getEdgeArrowSize() {
        return getDoubleProperty(PROPERTY_ARROW_SIZE, DEFAULT_ARROW_SIZE);
    }


    private double getDoubleProperty(String propertyName, double defaultValue) {
        String p = properties.getProperty(propertyName, Double.toString(defaultValue));
        try {
            return Double.valueOf(p);
        } catch (NumberFormatException e) {
            System.err.printf("Error in reading property %s: %s", propertyName, e.getMessage());
            return defaultValue;
        }

    }

    private boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        String p = properties.getProperty(propertyName, Boolean.toString(defaultValue));
        try {
            return Boolean.valueOf(p);
        } catch (NumberFormatException e) {
            System.err.printf("Error in reading property %s: %s", propertyName, e.getMessage());
            return defaultValue;
        }
    }


    public static void main(String[] args) {
        GraphProperties props = new GraphProperties();
        System.out.println("Prop vertex radius: " + props.getVertexRadius());
        System.out.println("Prop vertex use label: " + props.getUseVertexLabel());
    }
}