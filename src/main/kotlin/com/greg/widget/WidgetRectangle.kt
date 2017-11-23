package com.greg.widget

import com.greg.selection.DragModel
import javafx.scene.shape.Rectangle

class WidgetRectangle(x: Double, y: Double, width: Double, height: Double) : Rectangle(x, y, width, height), Widget {
    override lateinit var drag: DragModel
}