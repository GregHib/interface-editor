package com.greg.ui.canvas.state.states.edit.resize.observer

import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.shape.Rectangle

class AttributeObserver(observer: WidgetObserver) {
    private val xListener = Listener(observer)
    private val yListener = Listener(observer)
    private val widthListener = Listener(observer)
    private val heightListener = Listener(observer)
    private var widget: WidgetGroup? = null

    fun link(widget: WidgetGroup) {
        this.widget = widget
        widget.getNode().layoutXProperty().addListener(xListener)
        widget.getNode().layoutYProperty().addListener(yListener)
        val rect = widget.getRectangle().getNode() as Rectangle
        rect.widthProperty().addListener(widthListener)
        rect.heightProperty().addListener(heightListener)
    }

    fun unlink() {
        widget?.getNode()?.layoutXProperty()?.removeListener(xListener)
        widget?.getNode()?.layoutYProperty()?.removeListener(yListener)
        val rect = widget?.getRectangle()?.getNode() as Rectangle
        rect.widthProperty().removeListener(widthListener)
        rect.heightProperty().removeListener(heightListener)
    }
}