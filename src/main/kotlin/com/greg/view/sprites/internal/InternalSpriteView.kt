package com.greg.view.sprites.internal

import com.greg.model.cache.CacheController
import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.SpriteDisplay
import com.greg.view.sprites.tree.ImageTreeItem
import javafx.collections.ListChangeListener
import javafx.scene.control.TreeItem

class InternalSpriteView(cache: CacheController) : SpriteDisplay("Sprites", WidgetType.SPRITE, { target: ImageTreeItem -> "${target.value}:${target.parent.value}"}) {

    init {
        ArchiveMedia.imageArchive.addListener(ListChangeListener { change ->
            change.next()
            if (change.wasAdded()) {
                for (archive in change.addedSubList) {
                    val name = cache.sprites.getName(archive.hash)
                    val archiveItem = TreeItem(name)

                    archive.sprites
                            .mapIndexed { index, sprite -> ImageTreeItem("$index", sprite) }
                            .forEach { archiveItem.children.add(it) }
                    rootTreeItem.children.add(archiveItem)
                }
            } else if (change.wasRemoved()) {
                //Find and remove
                for (archive in change.removed) {
                    val name = cache.sprites.getName(archive.hash)
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