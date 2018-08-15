package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.CacheController
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import com.greg.view.hierarchy.HierarchyItem
import com.greg.view.hierarchy.HierarchyView
import com.greg.view.sprites.internal.InternalSpriteView
import javafx.collections.ListChangeListener
import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.input.KeyEvent
import tornadofx.View
import tornadofx.splitpane
import tornadofx.tab
import tornadofx.tabpane


class LeftPane : View(), KeyInterface {

    val hierarchy = HierarchyView()

    private lateinit var tabPane: TabPane
    private val components = ComponentView()
    private val widgets: WidgetsController by inject()
    val cache: CacheController by inject()

    private val sprites = InternalSpriteView(cache)

    private fun addChildren(parent: HierarchyItem, widget: Widget) {
        val item = HierarchyItem("${widget.name} ${widget.identifier}", widget.identifier, widget)

        if (widget is WidgetContainer) {
            widget.getChildren().forEach { child -> addChildren(item, child) }
        }

        parent.children.add(item)
    }

    init {
        widgets.updateHierarchy.addListener { _, _, newValue ->
            if(newValue) {
                hierarchy.rootTreeItem.children.clear()

                val items = arrayListOf<HierarchyItem>()

                widgets.get().forEach { widget ->
                    val item = HierarchyItem("${widget.name} ${widget.identifier}", widget.identifier, widget)
                    (widget as? WidgetContainer)?.getChildren()?.forEach { child -> addChildren(item, child) }
                    items.add(item)
                }

                //Multi-add
                hierarchy.rootTreeItem.children.addAll(items)

                widgets.updateHierarchy.set(false)
            }
        }

        widgets.get().addListener(ListChangeListener { change ->
            change.next()
            //Get items changed
            val list = if (change.wasAdded()) change.addedSubList else change.removed

            //Sync hierarchy with widget list changes
            val items = arrayListOf<HierarchyItem>()

            list?.forEach { widget ->
                if (change.wasAdded()) {
                    val item = HierarchyItem("${widget.name} ${widget.identifier}", widget.identifier, widget)
                    if (widget is WidgetContainer) {
                        widget.getChildren().forEach { child -> addChildren(item, child) }
                    }
                    items.add(item)
                } else if (change.wasRemoved()) {
                    hierarchy.rootTreeItem.children.removeAll(
                            hierarchy.rootTreeItem.children
                                    .filterIsInstance<HierarchyItem>()
                                    .filter { it.identifier == widget.identifier }
                    )
                }
            }

            //Multi-add
            hierarchy.rootTreeItem.children.addAll(items)
        })
    }

    override val root = splitpane(Orientation.VERTICAL) {
        minWidth = 290.0
        prefWidth = 290.0
        tabPane = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Hierarchy") {
                add(components)
            }
            tab("Sprites") {
                add(sprites)
            }
        }
        add(hierarchy)
    }

    override fun handleKeyEvents(event: KeyEvent) {
        hierarchy.handleKeyEvents(event)
    }
}