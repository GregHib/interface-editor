package com.greg.ui.canvas.state.states.edit.resize.box.points

import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle

class ResizePoint(width: Double, height: Double) : Rectangle(width, height) {
    fun addCursor(cursor: Cursor) {
        addEventFilter<MouseEvent>(MouseEvent.ANY, { e ->
            if (e.eventType == MouseEvent.MOUSE_ENTERED) {
                parent.cursor = cursor
            } else if (e.eventType == MouseEvent.MOUSE_EXITED) {
                parent.cursor = Cursor.DEFAULT
            }
        })
    }
}