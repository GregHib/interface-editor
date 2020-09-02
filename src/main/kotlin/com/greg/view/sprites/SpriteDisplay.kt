package com.greg.view.sprites

import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.tree.ImageTreeCell
import com.greg.view.sprites.tree.ImageTreeItem
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.util.Callback
import tornadofx.View
import tornadofx.treeview

open class SpriteDisplay(root: String, type: WidgetType, getCopyString: (target: ImageTreeItem) -> (String)) : View() {

    protected val rootTreeItem = TreeItem(root)

    override val root = treeview(rootTreeItem) {
        cellFactory = Callback<TreeView<String>, TreeCell<String>> {
            ImageTreeCell()
        }

        //Begin drag from here to canvas
        setOnDragDetected { event ->
            val target = this.selectionModel.selectedItem
            if (target != null && target is ImageTreeItem) {
                val db = startDragAndDrop(TransferMode.MOVE)
                db.dragView = SwingFXUtils.toFXImage(target.sprite!!, null)
                val cc = ClipboardContent()
                cc.putString("${type.name}:${getCopyString(target)}")
                db.setContent(cc)
            }

            event.consume()
        }
    }
}