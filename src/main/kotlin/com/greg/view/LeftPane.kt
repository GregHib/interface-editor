package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.view.hierarchy.HierarchyItem
import com.greg.view.hierarchy.HierarchyView
import com.greg.view.sprites.SpriteController
import com.greg.view.sprites.external.ExternalSpriteView
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
    private val externalSprites = ExternalSpriteView()
    private val sprites = InternalSpriteView()

    private lateinit var tabPane: TabPane
    private val components = ComponentView()
    private val widgets: WidgetsController by inject()
    val controller: SpriteController by inject()


    fun loadImages() {
        controller.importBinary()
        tabPane.selectionModel.select(1)
    }

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
        tabPane = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("Hierarchy") {
                add(components)
            }
            tab("Sprites") {
                add(externalSprites)
            }
            tab("Cache sprites") {
                add(sprites)
            }
        }
        add(hierarchy)
    }

    override fun handleKeyEvents(event: KeyEvent) {
        hierarchy.handleKeyEvents(event)
    }
}