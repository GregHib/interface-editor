package com.greg.canvas.properties

import com.greg.canvas.WidgetCanvas

class PropertyHandler(var canvas: WidgetCanvas) {

    fun refresh() {
        println("Refresh properties panel")

        for (i in canvas.selectionGroup.getGroup()) {
            println(i)
        }
    }

}