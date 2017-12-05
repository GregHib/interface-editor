package com.greg.properties

import com.greg.canvas.widget.Widget
import com.greg.controller.Controller
import com.greg.properties.attributes.PropertyGroup
import javafx.scene.layout.VBox

class PropertyPanel(private var controller: Controller) {

    var groups: List<PropertyGroup>? = null

    fun refresh() {

        var group = controller.canvas.selectionGroup.getGroup()
        controller.propertyPanel.children.clear()

        groups = null

        when {
            group.size == 0 -> controller.propertyPanel.children.add(PropertyGroup("No Selection"))
            group.size == 1 -> loadProperties(group)
            else -> {
                var type = group.first().javaClass
                if(group.stream().allMatch({ e -> e.javaClass == type })) {
                    loadProperties(group)
                }
            }
        }
    }

    private fun loadProperties(widgets: MutableSet<Widget>) {
        if(groups == null) {
            var box = VBox()
            groups = widgets.first().getGroup()
            box.children.addAll(groups!!)
            controller.propertyPanel.children.addAll(box)
        }

        //Link
        if(groups != null) {
            for(widget in widgets) {
                var list = mutableListOf<PropertyGroup>()
                list.addAll(groups!!)
                widget.handleGroup(list)
            }
        }
    }
}