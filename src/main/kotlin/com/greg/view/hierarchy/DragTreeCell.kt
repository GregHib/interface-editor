package com.greg.view.hierarchy

import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.Label
import javafx.scene.control.TreeView
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.action
import tornadofx.move
import tornadofx.pane

class DragTreeCell(private val tree: TreeView<String>, root: CheckBoxTreeItem<String>) : CheckBoxTreeCell<String>() {

    override fun startEdit() {
        //Can't edit root
        if (tree.getRow(treeItem) == 0)
            cancelEdit()
        else
            super.startEdit()
    }

    private fun canDrop(cell: DragTreeCell): Boolean {
        //Can drop if not part of the current selection & not root
        val row = tree.getRow(cell.treeItem)
        return row != 0 && !tree.selectionModel.selectedIndices.contains(row)
    }

    private fun getTreeItem(target: EventTarget): DragTreeCell {
        var pal = target
        for(i in 0..5) {
            if(pal is DragTreeCell)
                return pal

            if(pal is Node)
                pal = pal.parent
        }
        return target as DragTreeCell
    }

    init {
        if (tree.getRow(treeItem) != 0) {
            setOnDragDetected { event ->

                if (item == null)
                    return@setOnDragDetected

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
                    val size = tree.selectionModel.selectedIndices.size
                    if(tree.selectionModel.selectedIndices.first() < index) {
                        val idx = tree.selectionModel.selectedIndices.first()
                        for (i in size downTo 1)
                            root.children.move(root.children[idx - 1], index - 1)
                        tree.selectionModel.clearSelection()
                        for(i in index - size + 1 until index + 1)
                            tree.selectionModel.select(i)
                    } else {
                        val idx = tree.selectionModel.selectedIndices.last()
                        for (i in size downTo 1)
                            root.children.move(root.children[idx - 1], index - 1)
                        tree.selectionModel.clearSelection()
                        tree.selectionModel.selectRange(index, index + size)
                    }

                    opacity = 1.0

                    success = true
                }
                event.isDropCompleted = success

                event.consume()
            }

            onDragDone = EventHandler<DragEvent> { it.consume() }
        }
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        // We only show the custom cell if it is a leaf, meaning it has
        // no children.
        if (!isEmpty && this.treeItem.isLeaf && treeItem is HierarchyItem) {

            val widget = (treeItem as HierarchyItem).widget
            // A custom HBox that will contain your check box, label and
            // button.
            val cellBox = HBox(5.0)
            val checkBox = CheckBox()
            checkBox.isSelected = widget.isLocked()//TODO why?
            val label = Label(treeItem.value)
            checkBox.action {
                widget.setLocked(checkBox.isSelected)
                widget.setSelected(false)
            }

            val space = pane {
                HBox.setHgrow(this, Priority.ALWAYS)
                setMinSize(10.0, 1.0)
            }

            cellBox.children.addAll(graphic, label, space, checkBox)

            // We set the cellBox as the graphic of the cell.
            graphic = cellBox
            text = null
        }
    }
}