package com.greg.controller.controller.canvas

import com.greg.controller.model.Widget
import com.greg.controller.view.CanvasView

class StateManager(val canvas: CanvasView) {
    var state: CanvasState = DefaultState(canvas)

    fun toggle() {
        state = if(state is DefaultState)
            EditState(canvas)
        else
            DefaultState(canvas)

    }

    fun edit(widget: Widget) {

    }
}