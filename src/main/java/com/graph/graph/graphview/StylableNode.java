package com.graph.graph.graphview;

public interface StylableNode {
    /**
     * Applies cumulatively the <code>css</code> inline styles to the node.
     * <p>
     * These inline JavaFX styles have higher priority and are not overwritten by
     * any css classes set by SmartStylableNode#addStyleClass(java.lang.String)
     * But will be discarded if you use  @link SmartStylableNode#setStyleClass(java.lang.String)
     * <p>
     * If you need to clear any previously set inline styles, use
     * <code>.setStyle(null)</code>
     *
     * @param css styles
     */
    public void setStyle(String css);

    /**
     * Applies the CSS styling defined in class selector <code>cssClass</code>.
     * <p>
     * The <code>cssClass</code> string must not contain a preceding dot, e.g.,
     * "myClass" instead of ".myClass".
     * <p>
     * The CSS Class must be defined in <code>smartpgraph.css</code> file or
     * in the custom provided stylesheet.
     * <p>
     * The expected behavior is to remove all current styling before
     * applying the class css.
     *
     * @param cssClass name of the CSS class.
     */
    public void setStyleClass(String cssClass);

    /**
     * Applies cumulatively the CSS styling defined in class selector
     * <code>cssClass</code>.
     * <p>
     * The CSS Class must be defined in <code>smartpgraph.css</code> file or
     * in the custom provided stylesheet.
     * <p>
     * The cumulative operation will overwrite any existing styling elements
     * previously defined for previous classes.
     *
     * @param cssClass name of the CSS class.
     */
    public void addStyleClass(String cssClass);

    /**
     * Removes a previously <code>cssClass</code> existing CSS styling.
     * <p>
     * Given styles can be added sequentially, the removal of a css class
     * will be a removal that keeps the previous ordering of kept styles.
     *
     * @param cssClass name of the CSS class.
     * @return true if successful; false if <code>cssClass</code> wasn't
     * previously set.
     */
    public boolean removeStyleClass(String cssClass);
}
