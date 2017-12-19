package com.greg.canvas.state.edit.resize

import com.greg.canvas.widget.Widget
import javafx.scene.shape.Rectangle

class WidgetChangeListener(changeInterface: WidgetChangeInterface) {
    private val xListener = Listener(changeInterface)
    private val yListener = Listener(changeInterface)
    private val widthListener = Listener(changeInterface)
    private val heightListener = Listener(changeInterface)
    private var widget: Widget? = null

    fun link(widget: Widget) {
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