package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.view.hierarchy.HierarchyItem
import com.greg.view.hierarchy.HierarchyView
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.util.Callback
import org.controlsfx.control.GridView
import org.controlsfx.control.cell.ColorGridCell
import tornadofx.View
import tornadofx.splitpane
import tornadofx.tab
import tornadofx.tabpane
import java.util.*


class LeftPane : View(), KeyInterface {

    val hierarchy = HierarchyView()
    private val components = ComponentView()
    private val widgets: WidgetsController by inject()

    init {
        widgets.getAll().addListener(ListChangeListener {
            it.next()
            //Get items changed
            val list = if (it.wasAdded()) it.addedSubList else it.removed

            //Sync hierarchy with widget list changes
            list?.forEach { widget ->
                if (it.wasAdded()) {
                    val item = HierarchyItem(widget.name, widget.identifier, widget)
                    hierarchy.rootTreeItem.children.add(item)
                    item.selectedProperty().bindBidirectional(widget.selectedProperty())
                } else if (it.wasRemoved()) {
                    hierarchy.rootTreeItem.children.removeAll(
                            hierarchy.rootTreeItem.children
                                    .filterIsInstance<HierarchyItem>()
                                    .filter { it.identifier == widget.identifier }
                    )
                }
            }
        })
    }

    override val root = splitpane(Orientation.VERTICAL) {
        minWidth = 290.0
        prefWidth = 290.0
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Hierarchy") {
                add(components)
            }
            tab("Sprites") {
                disableDelete()
                val list = FXCollections.observableArrayList<Color>()

                val colorGrid = GridView(list)

                colorGrid.cellFactory = Callback { ColorGridCell() }
                val r = Random(System.currentTimeMillis())
                for (i in 0..800) {
                    list.add(Color(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0))
                }
                add(colorGrid)
            }
        }
        add(hierarchy)
    }

    override fun handleKeyEvents(event: KeyEvent) {
        hierarchy.handleKeyEvents(event)
    }
}