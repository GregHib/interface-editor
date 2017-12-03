package com.greg.properties

import com.greg.canvas.widget.Widget
import com.greg.controller.Controller
import com.greg.properties.attributes.PropertyGroup
import javafx.scene.layout.VBox

class PropertyPanel(private var controller: Controller) {

    fun refresh() {

        var group = controller.canvas.selectionGroup.getGroup()
        controller.propertyPanel.children.clear()

        when {
            group.size == 0 -> controller.propertyPanel.children.add(PropertyGroup("No Selection"))
            group.size == 1 -> loadProperties(group.first())
            else -> for (i in group) {
                println(i)
            }
        }
    }

    private fun loadProperties(widget: Widget) {
        var box = VBox()
        box.children.addAll(widget.getGroup())
        controller.propertyPanel.children.addAll(box)
    }
}