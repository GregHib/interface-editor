package com.greg.view.sprites.internal

import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.SpriteController
import com.greg.view.sprites.SpriteDisplay
import com.greg.view.sprites.tree.ImageTreeItem
import javafx.collections.ListChangeListener
import javafx.scene.control.TreeItem

class InternalSpriteView : SpriteDisplay("Archive", WidgetType.SPRITE, { target: ImageTreeItem -> "${target.value}:${target.parent.value}"}) {

    private val controller: SpriteController by inject()

    init {
        SpriteController.imageArchiveList.addListener(ListChangeListener { change ->
            change.next()
            if (change.wasAdded()) {
                for (archive in change.addedSubList) {
                    val name = controller.getName(archive.hash)
                    val archiveItem = TreeItem(name)

                    archive.sprites
                            .mapIndexed { index, sprite -> ImageTreeItem("$index", sprite) }
                            .forEach { archiveItem.children.add(it) }
                    rootTreeItem.children.add(archiveItem)
                }
            } else if (change.wasRemoved()) {
                //Find and remove
                for (archive in change.removed) {
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