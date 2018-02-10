package com.greg.view.sprites.internal

import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.SpriteController
import javafx.collections.ListChangeListener
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.util.Callback
import tornadofx.View
import tornadofx.treeview

class InternalSpriteView : View() {
    private val controller: SpriteController by inject()
    private val rootTreeItem = TreeItem("Media")

    override val root = treeview(rootTreeItem) {
        cellFactory = Callback<TreeView<String>, TreeCell<String>> {
            ImageTreeCell()
        }
        setOnDragDetected { event ->

            val target = this.selectionModel.selectedItem
            if(target is ImageTreeItem) {
                val db = startDragAndDrop(TransferMode.MOVE)
                db.dragView = target.image
                val cc = ClipboardContent()
                cc.putString("${WidgetType.CACHE_SPRITE.name}:${target.value}:${target.parent.value}")
                db.setContent(cc)
            }

            event.consume()
        }
    }

    init {
        SpriteController.filteredInternal.addListener(ListChangeListener {
            it.next()
            if (it.wasAdded()) {
                for (archive in it.addedSubList) {
                    val decoded = controller.getName(archive.hash)
                    val name = decoded?.substring(0, decoded.length - 4) ?: archive.hash.toString()
                    val archiveItem = TreeItem(name)
                    //Add names
                    archive.sprites
                            .mapIndexed { index, sprite -> ImageTreeItem("$index", SwingFXUtils.toFXImage(sprite.toBufferedImage(), null)) }
                            .forEach { archiveItem.children.add(it) }
                    rootTreeItem.children.add(archiveItem)
                }
            }
        })
        rootTreeItem.isExpanded = true
    }
}