package com.greg.ui.actions

import com.greg.ui.canvas.widget.Widget

class ActionManager {
    val deleted = mutableListOf<Widget>()

    fun delete(widget: Widget) {
        deleted.add(widget)
    }
}