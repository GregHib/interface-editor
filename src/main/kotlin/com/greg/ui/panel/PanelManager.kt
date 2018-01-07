package com.greg.ui.panel

import com.greg.controller.ControllerView
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.column.Column
import javafx.scene.layout.VBox

class PanelManager(private var controller: ControllerView) {

    private var properties = Panel("Properties", PanelType.PROPERTIES)
    private var layout = Panel("Layout", PanelType.LAYOUT)

    init {
        controller.attributesPanel.panes.add(properties)
        controller.attributesPanel.panes.add(layout)
        controller.attributesPanel.expandedPane = properties
    }

    fun reload() {
        controller.attributesPanel.panes
                .filterIsInstance<Panel>()
                .forEach { pane -> reload(pane, controller.canvas.selection.get()) }
    }

    private fun reload(panel: Panel, widgets: MutableSet<WidgetGroup>) {
        panel.getPane().children.clear()

        panel.groups = null

        when {
            widgets.size == 0 -> panel.getPane().children.add(Column("No Selection", null))
            widgets.size == 1 -> loadProperties(panel, widgets)
            else -> {
                val type = widgets.first().javaClass
                //Display only if all selected are of the same type
                if(widgets.stream().allMatch({ e -> e.javaClass == type }))
                    loadProperties(panel, widgets)
            }
        }
    }

    private fun loadProperties(panel: Panel, widgets: MutableSet<WidgetGroup>) {

        //Load the property groups of the first object
        //First object will always be correct as selection is either 1 or of all the same type
        if(panel.groups == null) {
            val box = VBox()
            panel.groups = widgets.first().getGroups(panel.type)
            widgets.first().init(panel.groups!!, controller.widgets)
            box.children.addAll(panel.groups!!)
            panel.getPane().children.addAll(box)
        }

        //Link all selected objects to the property groups
        if(panel.groups != null)
            for(widget in widgets)
                widget.link(panel, controller.widgets)
    }
}