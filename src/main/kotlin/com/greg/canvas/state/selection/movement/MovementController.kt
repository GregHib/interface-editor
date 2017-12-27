package com.greg.canvas.state.selection.movement

import com.greg.Utils
import com.greg.canvas.DragModel
import com.greg.canvas.state.selection.SelectionGroup
import com.greg.canvas.state.selection.WidgetSelection
import com.greg.canvas.widget.Widget
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class MovementController(private val selectionGroup: SelectionGroup, private val canvasPane: Pane, private val selection: WidgetSelection?) {

    private val mouse = MouseMovementController(this, selectionGroup)
    private val keyboard = KeyMovementController(this)
    var cloned = false

    fun init(event: MouseEvent) {
        //If has items selected
        if (selectionGroup.size() > 0) {
            if(event.isShiftDown)
                clone()

            //Set info needed for drag just in case dragging occurs
            selectionGroup.getGroup().forEach { widget ->
                //save the offset of the shapes position relative to the mouse click
                startDrag(widget, event, canvasPane)
            }
        }
    }

    fun drag(event: MouseEvent, widget: Widget?) {
        if(event.isShiftDown)
            clone()

        mouse.drag(event, widget)
    }

    fun move(event: KeyEvent) {
        if(event.isShiftDown)
            clone()

        keyboard.move(event)
    }

    fun reset(code: KeyCode) {
        keyboard.reset(code)
    }

    fun move(x: Double, y: Double) {
        selectionGroup.getGroup().forEach { widget ->
            move(widget, x, y)
        }
    }

    fun resetClone() {
        cloned = false
    }

    private fun clone() {
        if(selection != null && !cloned) {
            selection.clone()
            cloned = true
        }
    }

    private fun move(widget: Widget, deltaX: Double, deltaY: Double) {
        moveWidget(widget, widget.getNode().layoutX + deltaX, widget.getNode().layoutY + deltaY)
    }

    fun moveWidget(widget: Widget, targetX: Double, targetY: Double) {
        //Bounds of the container
        val bounds = canvasPane.localToScene(canvasPane.layoutBounds)

        //Size of shape
        val width = widget.getRectangle().getNode().layoutBounds.width
        val height = widget.getRectangle().getNode().layoutBounds.height

        //TODO move set constraints to a widget override function for setting layoutX/Y?

        //Constrain position to within the container
        val x = Utils.constrain(targetX, bounds.width - width)
        val y = Utils.constrain(targetY, bounds.height - height)

        //Move
        widget.getNode().layoutX = x
        widget.getNode().layoutY = y
    }


    fun startDrag(widget: Widget, event: MouseEvent, canvasPane: Pane) {
        //TODO these seems better but is off by like .25?
        //widget.getNode().layoutX - event.x
        //widget.getNode().layoutY - event.y
        val offsetX = canvasPane.localToScene(widget.getNode().boundsInParent).minX - event.sceneX
        val offsetY = canvasPane.localToScene(widget.getNode().boundsInParent).minY - event.sceneY
//            println("${canvas.canvasPane.localToScene(widget.boundsInParent).minX} ${event.sceneX} ${event.x} ${widget.getNode().layoutX}")
        widget.drag = DragModel(offsetX, offsetY)
    }
}