package com.greg.canvas.widget

import com.greg.canvas.DragModel
import javafx.scene.Group
import javafx.scene.paint.Color

abstract class Widget: Group() {

    lateinit var drag: DragModel
    abstract fun setStroke(colour: Color?)

}