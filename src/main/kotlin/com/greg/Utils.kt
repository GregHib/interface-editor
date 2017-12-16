package com.greg

import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.Widget
import javafx.scene.input.MouseEvent
import kotlin.math.round

class Utils {
    companion object {

        fun constrain(value: Double, max: Double): Double {
            return constrain(value, 0.0, max)
        }

        fun constrain(value: Double, min: Double, max: Double): Double {
            return round(if (value < min) min else if (value > max) max else value)
        }

        fun moveInCanvas(event: MouseEvent, canvas: WidgetCanvas, widget: Widget) {
            //Bounds of the container
            val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)

            //The actual positioning of the shape relative to the container
            var x = event.x + widget.drag!!.offsetX!!
            var y = event.y + widget.drag!!.offsetY!!

            //Size of shape
            val width = widget.getRectangle().getNode().layoutBounds.width
            val height = widget.getRectangle().getNode().layoutBounds.height

            //Constrain position to within the container
            //TODO move constraints to a relocate override?
            x = constrain(x, bounds.width - width)
            y = constrain(y, bounds.height - height)

            //Move
            widget.getNode().layoutX = x
            widget.getNode().layoutY = y
        }

        fun setWidgetDrag(widget: Widget, event: MouseEvent, canvas: WidgetCanvas) {
            //TODO these seems better but is off by like .25?
            //widget.getNode().layoutX - event.x
            //widget.getNode().layoutY - event.y
            val offsetX = canvas.canvasPane.localToScene(widget.getNode().boundsInParent).minX - event.sceneX
            val offsetY = canvas.canvasPane.localToScene(widget.getNode().boundsInParent).minY - event.sceneY
//            println("${canvas.canvasPane.localToScene(widget.boundsInParent).minX} ${event.sceneX} ${event.x} ${widget.getNode().layoutX}")
            widget.drag = DragModel(offsetX, offsetY)
        }
    }
}