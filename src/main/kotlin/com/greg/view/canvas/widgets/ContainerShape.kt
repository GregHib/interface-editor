package com.greg.view.canvas.widgets

import javafx.scene.Group
import javafx.scene.shape.Rectangle
import tornadofx.add

class ContainerShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    val group = Group()

    private val clipRectangle = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())
    init {
        add(group)
        clipRectangle.widthProperty().bind(outline.widthProperty())
        clipRectangle.heightProperty().bind(outline.heightProperty())
        this.clip = clipRectangle
    }
}