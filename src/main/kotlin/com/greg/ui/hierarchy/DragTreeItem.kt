package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import com.greg.ui.action.change.ChangeType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.control.TreeView
import javafx.scene.control.cell.TextFieldTreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.text.Text
import javafx.util.converter.DefaultStringConverter
import tornadofx.move

class DragTreeItem(private val tree: TreeView<String>, controller: ControllerView) : TextFieldTreeCell<String?>(DefaultStringConverter()) {

    override fun startEdit() {
        //Can't edit root
        if (tree.getRow(treeItem) == 0)
            cancelEdit()
        else
            super.startEdit()
    }

    private fun canDrop(cell: DragTreeItem): Boolean {
        //Can drop if not part of the current selection & not root
        val row = tree.getRow(cell.treeItem)
        return row != 0 && !tree.selectionModel.selectedIndices.contains(row)
    }

    private fun getTreeItem(target: EventTarget): DragTreeItem {
        return if (target is Text && target.parent is DragTreeItem)
            target.parent as DragTreeItem
        else
            target as DragTreeItem
    }

    init {
        //Root item isn't editable
        if (tree.getRow(treeItem) != 0) {
            setOnDragDetected { event ->

                if (item == null) {
                    return@setOnDragDetected
                }

                val dragboard = startDragAndDrop(TransferMode.MOVE)
                val content = ClipboardContent()

                content.putString("")

                dragboard.setContent(content)

                event.consume()
            }

            setOnDragOver { event ->
                if (canDrop(getTreeItem(event.target)) && event.dragboard.hasString())
                    event.acceptTransferModes(TransferMode.MOVE)

                event.consume()
            }

            setOnDragEntered { event ->
                if (canDrop(getTreeItem(event.target)) && event.dragboard.hasString())
                    opacity = 0.3
            }

            setOnDragExited { event ->
                if (canDrop(getTreeItem(event.target)) && event.dragboard.hasString())
                    opacity = 1.0
            }

            setOnDragDropped { event ->
                if (item == null) {
                    return@setOnDragDropped
                }

                val db = event.dragboard
                var success = false

                if (db.hasString()) {
                    val all = controller.widgets.getAll()
                    for(i in tree.selectionModel.selectedIndices) {
                        all.move(all[all.size - i], all.size - index)
                        controller.widgets.manager.record(ChangeType.ORDER, (all[all.size - i] as WidgetGroup).identifier, listOf(all.size - i, all.size - index))
                    }

                    controller.hierarchy.reload()

                    opacity = 1.0

                    success = true
                }
                event.isDropCompleted = success

                event.consume()
            }

            onDragDone = EventHandler<DragEvent> { it.consume() }
        }
    }
}