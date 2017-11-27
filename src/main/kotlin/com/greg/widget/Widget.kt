package com.greg.widget

import com.greg.selection.DragModel
import javafx.scene.Group
import javafx.scene.paint.Color

abstract class Widget: Group() {

    lateinit var drag: DragModel
    abstract fun setStroke(colour: Color?)

}