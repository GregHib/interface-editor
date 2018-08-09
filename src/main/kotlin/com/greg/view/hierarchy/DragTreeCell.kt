package com.greg.view.hierarchy

import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TreeCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import tornadofx.action
import tornadofx.checkbox
import tornadofx.move

class DragTreeCell : TreeCell<String>() {

    private var setup = false

    override fun startEdit() {
        //Can't edit root
        if (treeView.getRow(treeItem) == 0)
            cancelEdit()
        else
            super.startEdit()
    }

    private fun canDrop(cell: DragTreeCell): Boolean {
        //Can drop if not part of the current selection & not root
        val row = treeView.getRow(cell.treeItem)
        return row != 0 && !treeView.selectionModel.selectedIndices.contains(row)
    }

    private fun getTreeItem(target: EventTarget): DragTreeCell {
        var pal = target
        for (i in 0..5) {
            if (pal is DragTreeCell)
                return pal

            if (pal is Node)
                pal = pal.parent
        }
        return target as DragTreeCell
    }

    private fun setup() {
        if (setup)
            return

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
                val size = treeView.selectionModel.selectedIndices.size
                if (treeView.selectionModel.selectedIndices.first() < index) {
                    val idx = treeView.selectionModel.selectedIndices.first()
                    for (i in size downTo 1)
                        treeView.root.children.move(treeView.root.children[idx - 1], index - 1)
                    treeView.selectionModel.clearSelection()
                    for (i in index - size + 1 until index + 1)
                        treeView.selectionModel.select(i)
                } else {
                    val idx = treeView.selectionModel.selectedIndices.last()
                    for (i in size downTo 1)
                        treeView.root.children.move(treeView.root.children[idx - 1], index - 1)
                    treeView.selectionModel.clearSelection()
                    treeView.selectionModel.selectRange(index, index + size)
                }

                opacity = 1.0

                success = true
            }
            event.isDropCompleted = success

            event.consume()
        }

        onDragDone = EventHandler<DragEvent> { it.consume() }

        setup = true
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        //Only show extra check box's if has no children
        if(isEmpty) {
            graphic = null
            return
        }

        val label = Label(treeItem.value)

        graphic = when (treeItem) {
            treeView.root -> label
            is HierarchyItem -> {
                val cellBox = HBox(5.0)

                val widget = (treeItem as HierarchyItem).widget

                setup()

                val box = checkbox {
                    selectedProperty().bindBidirectional(widget.selectedProperty())

                    action {
                        //Widget select everything highlighted in hierarchy
                        treeView.selectionModel.selectedItems
                                .filterIsInstance<HierarchyItem>()
                                .filter { it.widget != widget }
                                .forEach {
                                    it.widget.setSelected(isSelected)
                                }
                    }

                    selectedProperty().addListener { _, _, newValue ->
                        //Deselect checkbox if locked
                        if (newValue && widget.isLocked())
                            isSelected = false

                        //Hierarchy highlight if not already
                        if (treeItem != null) {
                            val index = treeView.selectionModel.selectedItems.indexOf(treeItem)
                            if (index == -1) {
                                treeView.selectionModel.clearSelection()
                                treeView.selectionModel.select(treeItem)
                            }
                        }
                    }
                }

                cellBox.children.addAll(box, label)

                cellBox
            }
            else -> {
                null
            }
        }
    }
}