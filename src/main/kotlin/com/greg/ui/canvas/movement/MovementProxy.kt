package com.greg.ui.canvas.movement

import com.greg.Utils.Companion.constrain
import com.greg.ui.canvas.movement.input.KeyMovement
import com.greg.ui.canvas.movement.input.MouseMovement
import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class MovementProxy(private val pane: Pane, private val selection: Selection) {

    private val mouse = MouseMovement(this, selection)
    private val keyboard = KeyMovement(this)
    var cloned = false

    fun init(event: MouseEvent): Boolean {
        var cloned = false
        //If has items selected
        if (selection.size() > 0) {
            if(event.isShiftDown) {
                clone()
                cloned = true
            }

            //Set info needed for drag just in case dragging occurs
            selection.get().forEach { widget ->
                //save the offset of the shapes position relative to the mouse click
                start(widget, event, pane)
            }
        }
        return !cloned
    }

    fun drag(event: MouseEvent) {
        mouse.drag(event)
    }

    fun move(event: KeyEvent) {
        keyboard.move(event)
    }

    fun reset(code: KeyCode) {
        keyboard.reset(code)
    }

    fun move(x: Double, y: Double) {
        selection.get().forEach { widget ->
            move(widget, x, y)
        }
    }

    fun resetClone() {
        cloned = false
    }

    fun clone() {
        if(!cloned) {
            selection.clone()
            cloned = true
        }
    }

    private fun move(widget: WidgetGroup, deltaX: Double, deltaY: Double) {
        moveWidget(widget, widget.getNode().layoutX + deltaX, widget.getNode().layoutY + deltaY)
    }

    fun moveWidget(widget: WidgetGroup, targetX: Double, targetY: Double) {
        //Bounds of the container
        val bounds = pane.localToScene(pane.layoutBounds)

        //Size of shape
        val width = widget.getRectangle().getNode().layoutBounds.width
        val height = widget.getRectangle().getNode().layoutBounds.height

        //Constrain position to within the container
        val x = constrain(targetX, bounds.width - width)
        val y = constrain(targetY, bounds.height - height)

        //Move
        widget.getNode().layoutX = x
        widget.getNode().layoutY = y
    }


    fun start(widget: WidgetGroup, event: MouseEvent, pane: Pane) {
        val offsetX = pane.localToScene(widget.getNode().boundsInParent).minX - event.sceneX
        val offsetY = pane.localToScene(widget.getNode().boundsInParent).minY - event.sceneY
        start(widget, offsetX, offsetY)
    }

    fun start(widget: WidgetGroup, x: Double, y: Double) {
        widget.start = StartPoint(x, y)
    }

    fun getClone(event: MouseEvent): WidgetGroup? {
        return selection.get().firstOrNull { it.boundsInParent.intersects(event.x, event.y, 1.0, 1.0) }
    }
}