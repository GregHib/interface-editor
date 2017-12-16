package com.greg.panels.attributes.parts

import com.greg.canvas.widget.Widget
import com.greg.controller.Controller
import com.greg.panels.attributes.parts.pane.AttributePane
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.scene.layout.VBox

class AttributesPanel(private var controller: Controller) {

    private var properties = AttributePane("Properties", AttributePaneType.PROPERTIES)
    private var layout = AttributePane("Layout", AttributePaneType.LAYOUT)

    init {
        controller.attributesPanel.panes.add(properties)
        controller.attributesPanel.panes.add(layout)
        controller.attributesPanel.expandedPane = properties
    }

    fun reload() {
        controller.attributesPanel.panes
                .filterIsInstance<AttributePane>()
                .forEach { pane -> reload(pane, controller.canvas.selectionGroup.getGroup()) }
    }

    private fun reload(pane: AttributePane, widgets: MutableSet<Widget>) {
        pane.getPane().children.clear()

        pane.groups = null

        when {
            widgets.size == 0 -> pane.getPane().children.add(AttributeGroup("No Selection", null))
            widgets.size == 1 -> loadProperties(pane, widgets)
            else -> {
                val type = widgets.first().javaClass
                //Display only if all selected are of the same type
                if(widgets.stream().allMatch({ e -> e.javaClass == type }))
                    loadProperties(pane, widgets)
            }
        }
    }

    private fun loadProperties(pane: AttributePane, widgets: MutableSet<Widget>) {

        //Load the property groups of the first object
        //First object will always be correct as selection is either 1 or of all the same type
        if(pane.groups == null) {
            val box = VBox()
            pane.groups = widgets.first().getGroups(pane.type)
            widgets.first().init(pane.groups!!)
            box.children.addAll(pane.groups!!)
            pane.getPane().children.addAll(box)
        }

        //Link all selected objects to the property groups
        if(pane.groups != null)
            for(widget in widgets)
                widget.link(pane)
    }
}