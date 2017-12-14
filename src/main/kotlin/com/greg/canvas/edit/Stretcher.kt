package com.greg.canvas.edit

import com.greg.App
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle

class Stretcher(width: Double, height: Double) : Rectangle(width, height) {
    fun addCursor(cursor: Cursor) {
        addEventFilter<MouseEvent>(MouseEvent.ANY, { e ->
            if (e.eventType == MouseEvent.MOUSE_ENTERED) {
                App.mainStage.scene.cursor = cursor
            } else if (e.eventType == MouseEvent.MOUSE_EXITED) {
                App.mainStage.scene.cursor = Cursor.DEFAULT
            }
        })
    }


}