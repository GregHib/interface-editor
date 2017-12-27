package com.greg.canvas.state.selection.movement

import com.greg.canvas.state.selection.SelectionGroup
import com.greg.canvas.widget.Widget
import javafx.scene.input.MouseEvent

class MouseMovementController(private val controller: MovementController, private val selectionGroup: SelectionGroup) {

    fun drag(event: MouseEvent, target: Widget?) {
        if (target != null && selectionGroup.contains(target)) {
            selectionGroup.getGroup().forEach { widget ->
                moveEvent(widget, event)
            }
        }
    }

    private fun moveEvent(widget: Widget, event: MouseEvent) {
        //The actual positioning of the shape with mouse offset corrected
        var x = event.x + widget.drag!!.offsetX!!
        var y = event.y + widget.drag!!.offsetY!!

        controller.moveWidget(widget, x, y)
    }
}