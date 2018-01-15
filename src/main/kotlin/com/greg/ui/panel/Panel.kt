package com.greg.ui.panel

import com.greg.controller.controller.WidgetsController
import com.greg.controller.model.Widget
import com.greg.controller.view.PanelGroup
import com.greg.controller.view.PanelRow
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.column.Column
import javafx.geometry.Insets
import javafx.scene.layout.AnchorPane
import tornadofx.*

class Panel(var type: PanelType) : View() {

    val content: AnchorPane = anchorpane()
    private var groups = listOf<PanelGroup>()

    override val root = stackpane {
        padding = Insets(0.0)
        add(content)
    }

    var columns: List<Column>? = null

    fun reload(widgets: WidgetsController) {
        val primaryWidget = widgets.getSelection().firstOrNull()

        groups
                .filter { it.rows().size > 0 }
                .forEach { group ->
                    widgets.getAll().forEach { select ->
                        var index = 0
                        select.properties.get()//TODO type won't work with inherited widget types
                                .filter { property -> property.pane == type }
                                .forEach { property ->
                                    val row = group.rows()[index++]
                                    row.unlink(property.property)
                                }
                    }
                }

        content.clear()

        with(content) {
            vbox {
                groups = createGroups(primaryWidget)
                groups.forEach { group ->
                    add(group.root)

                    if (primaryWidget != null) {
                        widgets.forSelected { widget ->

                            val primary = widget == primaryWidget

                            var index = 0
                            widget.properties.get()
                                    .filter { property -> property.pane == type && property.type == WidgetType.WIDGET }
                                    .forEach { property ->
                                        val row = group.rows()[index++]
                                        row.link(property.property, primary)
                                    }


                            widget.properties.get()//TODO type won't work with inherited widget types
                                    .filter { property -> property.pane == type && property.type == primaryWidget.type }
                                    .forEach { property ->
                                        val row = group.rows()[index++]
                                        row.link(property.property, primary)
                                    }
                        }
                    }
                }
            }
        }
    }


    private fun createGroups(widget: Widget?): List<PanelGroup> {
        val list = mutableListOf<PanelGroup>()

        if (widget == null) {
            list.add(PanelGroup("No Selection", false))
        } else {
            WidgetType.values().forEach { widgetType ->
                val group = PanelGroup(widgetType.type ?: "Null")
                widget.properties.get().forEach { property ->
                    if (type == property.pane && widgetType == property.type) {
                        val row = PanelRow(property.property.name.capitalize())
                        row.create(property.property)
                        group.addRow(row)
                    }
                }

                if (group.rows().size > 0)
                    list.add(group)
            }
        }

        return list
    }
}