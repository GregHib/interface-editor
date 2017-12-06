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
            group.size == 0 -> controller.propertyPanel.children.add(PropertyGroup("No Selection", null))
            group.size == 1 -> loadProperties(group)
            else -> {
                var type = group.first().javaClass
                //Display only if all selected are of the same type
                if(group.stream().allMatch({ e -> e.javaClass == type }))
                    loadProperties(group)
            }
        }
    }

    private fun loadProperties(widgets: MutableSet<Widget>) {

        //Load the property groups of the first object
        //First object will always be correct as selection is either 1 or of all the same type
        if(groups == null) {
            var box = VBox()
            groups = widgets.first().getGroups()
            box.children.addAll(groups!!)
            controller.propertyPanel.children.addAll(box)
        }

        //Link all selected objects to the property groups
        if(groups != null) {
            for(widget in widgets) {
                var list = mutableListOf<PropertyGroup>()
                list.addAll(groups!!)
                widget.handleGroup(list)
            }
        }
    }
}