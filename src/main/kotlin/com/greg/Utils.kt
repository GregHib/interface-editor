package com.greg

import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.Widget
import javafx.scene.input.MouseEvent

class Utils {
    companion object {

        fun constrain(value: Double, max: Double): Double {
            return constrain(value, 0.0, max)
        }

        fun constrain(value: Double, min: Double, max: Double): Double {
            return if (value < min) min else if (value > max) max else value
        }

        fun moveInCanvas(event: MouseEvent, canvas: WidgetCanvas, widget: Widget) {
            //Bounds of the container
            val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)//TODO what's the difference between this and getCanvasX/Y

            //The actual positioning of the shape relative to the container
            var x = event.sceneX - bounds.minX + widget.drag!!.offsetX!!
            var y = event.sceneY - bounds.minY + widget.drag!!.offsetY!!

            //Size of shape
            val width = widget.layoutBounds.width
            val height = widget.layoutBounds.height

            //Constrain position to within the container
            //TODO move constraints to a relocate override?
            x = constrain(x, bounds.width - width)
            y = constrain(y, bounds.height - height)

            //Move
            widget.relocate(x, y)
        }

        fun setWidgetDrag(widget: Widget, event: MouseEvent, canvas: WidgetCanvas) {
            val offsetX = canvas.canvasPane.localToScene(widget.boundsInParent).minX - event.sceneX
            val offsetY = canvas.canvasPane.localToScene(widget.boundsInParent).minY - event.sceneY
            widget.drag = DragModel(offsetX, offsetY)
        }
    }
}