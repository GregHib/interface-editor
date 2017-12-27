package com.greg.ui.canvas.movement.input

import com.greg.ui.canvas.movement.MovementProxy
import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.MouseEvent

class MouseMovement(private val movement: MovementProxy, private val selection: Selection) {

    fun drag(event: MouseEvent) {
        selection.get().forEach { widget ->
            moveEvent(widget, event)
        }
    }

    private fun moveEvent(widget: WidgetGroup, event: MouseEvent) {
        //The actual positioning of the shape with mouse offset corrected
        val x = event.x + widget.start!!.offsetX!!
        val y = event.y + widget.start!!.offsetY!!

        movement.moveWidget(widget, x, y)
    }
}