package com.greg.controller.canvas

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.layout.Pane

/**
 * The canvas which holds all of the nodes of the application.
 */
class PannableCanvas : Pane() {

    private var myScale: DoubleProperty = SimpleDoubleProperty(1.0)

    /**
     * Set x/y scale
     */
    var scale: Double
        get() = myScale.get()
        set(scale) = myScale.set(scale)

    init {

        setPrefSize(600.0, 600.0)

        style = "-fx-background-color: lightgrey;"

        // add scale transform
        scaleXProperty().bind(myScale)
        scaleYProperty().bind(myScale)
    }

    /**
     * Set x/y pivot points
     * @param x
     * @param y
     */
    fun setPivot(x: Double, y: Double) {
        translateX -= x
        translateY -= y
    }
}