package com.greg.canvas.state

import com.greg.Utils.Companion.constrain
import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.Widget
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

interface PaneController {

    val canvas: WidgetCanvas

    fun handleMousePress(event: MouseEvent)

    fun handleMouseDrag(event: MouseEvent)

    fun handleMouseRelease(event: MouseEvent)

    fun handleDoubleClick(event: MouseEvent)

    fun handleMouseClick(event: MouseEvent)

    fun handleKeyPress(event: KeyEvent)

    fun handleKeyRelease(event: KeyEvent)

    fun refresh()

    /**
     * Functions
     */

    fun moveInCanvas(widget: Widget, event: MouseEvent) {
        //The actual positioning of the shape with mouse offset corrected
        var x = event.x + widget.drag!!.offsetX!!
        var y = event.y + widget.drag!!.offsetY!!

        moveInCanvas(widget, x, y)
    }

    fun move(widget: Widget, deltaX: Double, deltaY: Double) {
        moveInCanvas(widget, widget.getNode().layoutX + deltaX, widget.getNode().layoutY + deltaY)
    }

    fun moveInCanvas(widget: Widget, targetX: Double, targetY: Double) {
        //Bounds of the container
        val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)

        //Size of shape
        val width = widget.getRectangle().getNode().layoutBounds.width
        val height = widget.getRectangle().getNode().layoutBounds.height

        //TODO move set constraints to a widget override function for setting layoutX/Y?

        //Constrain position to within the container
        val x = constrain(targetX, bounds.width - width)
        val y = constrain(targetY, bounds.height - height)

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