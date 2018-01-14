package com.greg.ui.panel

import com.greg.controller.ControllerView
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.column.Column
import javafx.scene.layout.VBox
import tornadofx.*

class PanelManager(controller: ControllerView) : View() {

    private val panels = mutableListOf<Panel>()
    private val widgets = controller.widgets
    private val canvas = controller.canvas

    init {
        panels.add(Panel(PanelType.PROPERTIES))
        panels.add(Panel(PanelType.LAYOUT))
        reload()
    }

    override val root = scrollpane(fitToWidth = true) {
        squeezebox {
            for (panel in panels)
                fold(panel.type.name.toLowerCase().capitalize(), expanded = true) {
                    isAnimated = false
                    add(panel)
                }
        }
    }

    fun reload() {
        panels.forEach { pane ->
            reload(pane, canvas.selection.get())
        }
    }

    private fun reload(panel: Panel, widgets: List<WidgetGroup>) {
        panel.content.clear()

        panel.groups = null

        when {
            widgets.isEmpty() -> panel.content.add(Column("No Selection", null, false))
            widgets.size == 1 -> loadProperties(panel, widgets)
            else -> {
                val type = widgets.first().javaClass
                //Display only if all selected are of the same type
                if (widgets.stream().allMatch({ e -> e.javaClass == type }))
                    loadProperties(panel, widgets)
            }
        }
    }

    private fun loadProperties(panel: Panel, widgets: List<WidgetGroup>) {

        //Load the property groups of the first object
        //First object will always be correct as selection is either 1 or of all the same type
        if (panel.groups == null) {
            val box = VBox()
            panel.groups = widgets.first().getGroups(panel.type)
            widgets.first().init(panel.groups!!, this.widgets)
            for(node in panel.groups!!)
                box.children.add(node.root)
            panel.content.add(box)
        }

        //Link all selected objects to the property groups
        if (panel.groups != null)
            for (widget in widgets)
                widget.link(panel, this.widgets)
    }
}