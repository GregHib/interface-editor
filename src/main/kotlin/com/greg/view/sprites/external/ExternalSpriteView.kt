package com.greg.view.sprites.external

import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.tree.ImageTreeItem
import com.greg.view.sprites.SpriteController
import com.greg.view.sprites.SpriteDisplay
import javafx.collections.ListChangeListener
import javafx.scene.control.TreeItem

class ExternalSpriteView : SpriteDisplay("Sprite cache", WidgetType.SPRITE, { target: ImageTreeItem -> target.value }) {
    private val controller: SpriteController by inject()

    init {
        SpriteController.observableExternal.addListener(ListChangeListener {
            it.next()
            if (it.wasAdded()) {
                for (archive in it.addedSubList) {
                    val name = controller.getName(archive.hash)
                    val archiveItem = TreeItem(name)

                    archive.sprites
                            .mapIndexed { index, sprite -> ImageTreeItem("$index", sprite) }
                            .forEach { archiveItem.children.add(it) }
                    rootTreeItem.children.add(archiveItem)
                }
            } else if (it.wasRemoved()) {
                //Find and remove
                for (archive in it.removed) {
                    val name = controller.getName(archive.hash)
                    for (child in rootTreeItem.children) {
                        if (name == child.value) {
                            rootTreeItem.children.remove(child)
                            break
                        }
                    }
                }
            }
        })
        rootTreeItem.isExpanded = true
    }
}