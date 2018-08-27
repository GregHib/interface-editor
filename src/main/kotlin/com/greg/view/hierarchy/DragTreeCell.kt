package com.greg.view.hierarchy

import com.greg.controller.widgets.WidgetsController
import com.greg.controller.widgets.WidgetsController.Companion.widgets
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.TreeCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.action
import tornadofx.checkbox
import tornadofx.pane

class DragTreeCell : TreeCell<String>() {

    private var setup = false

    override fun startEdit() {
        //Can't edit root
        if (treeView.getRow(treeItem) == 0)
            cancelEdit()
        else
            super.startEdit()
    }

    private fun setup() {
        if (setup)
            return

        setOnDragDetected { event ->
            if (item == null)
                return@setOnDragDetected

            val board = startDragAndDrop(TransferMode.MOVE)
            val content = ClipboardContent()

            content.putString("")

            board.setContent(content)

            event.consume()
        }

        setOnDragOver { event ->
            if (event.dragboard.hasString() && event.dragboard.string.isEmpty())
                event.acceptTransferModes(TransferMode.MOVE)

            event.consume()
        }

        setOnDragEntered { event ->
            if (event.dragboard.hasString() && event.dragboard.string.isEmpty())
                opacity = 0.3
        }

        setOnDragExited { event ->
            if (event.dragboard.hasString() && event.dragboard.string.isEmpty())
                opacity = 1.0
        }


        setOnDragDropped { event ->
            if (item == null) {
                return@setOnDragDropped
            }

            val db = event.dragboard
            var success = false

            if (db.hasString()) {

                val target = treeView.getTreeItem(index)
                try {
                    //For multiple selections, reverse list and run for each
                    val filtered = treeView.selectionModel.selectedItems.filterIsInstance<HierarchyItem>()
                    val selected = filtered.map { it.widget }

                    //Have to get parent before removing stuff
                    val targetParent = target.parent

                    //Check if to add selection above or below the target
                    val addAbove = treeView.selectionModel.selectedIndices.min() ?: 0 > index

                    //Remove all selected widgets from their parents
                    filtered.map { it.parent }.forEachIndexed { index, treeItem ->
                        if(treeItem is HierarchyItem) {//If container
                            (treeItem.widget as WidgetContainer).getChildren().remove(selected[index])
                        } else if(treeItem == treeView.root) {//If root
                            widgets.remove(selected[index])
                        }
                    }

                    //Now that all selected widgets have been removed, now we need to add back to where the target is
                    if(target is HierarchyItem) {
                        val targetWidget = target.widget
                        if(targetWidget is WidgetContainer) {//if target is a container
                            targetWidget.getChildren().addAll(targetWidget.getChildren().size, selected)//Add to the end
                        } else {//or just a regular widget
                            if(targetParent is HierarchyItem) {//if target parent is container
                                //Add to target parent at index of target
                                val container = (targetParent.widget as WidgetContainer)
                                val index = container.getChildren().indexOf(targetWidget) + if(addAbove) 0 else 1
                                if(index >= container.getChildren().size)
                                    container.getChildren().addAll(selected)
                                else
                                    container.getChildren().addAll(container.getChildren().indexOf(targetWidget) + if(addAbove) 0 else 1, selected)
                            } else if(targetParent == treeView.root) {
                                //Add to root aka widgets at index of target
                                val index = widgets.indexOf(targetWidget) + if(addAbove) 0 else 1
                                if(index >= widgets.size())
                                    widgets.addAll(*selected.toTypedArray())
                                else
                                    widgets.addAll(widgets.indexOf(targetWidget) + if(addAbove) 0 else 1, selected)
                            }
                        }
                    } else if(target == treeView.root) {
                        //If target is the "root" send to the end
                        widgets.addAll(widgets.size() - 1, selected)//TODO toggle for adding widgets to end or start of containers?
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                treeView.selectionModel.clearSelection()

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
        if (isEmpty) {
            graphic = null
            return
        }

        if (treeItem is HierarchyItem || treeItem == treeView.root) {
            val label = Label(treeItem.value)

            val space = pane {
                HBox.setHgrow(this, Priority.ALWAYS)
                setMinSize(10.0, 1.0)
            }

            val cellBox = HBox(5.0)

            when (treeItem) {
                treeView.root -> {
                    val eye = ImageView(Image(javaClass.getResourceAsStream("eye.png")))
                    val lock = ImageView(Image(javaClass.getResourceAsStream("lock.png")))
                    cellBox.children.addAll(label, space, eye, pane {}, lock, pane {})
                }
                is HierarchyItem -> {
                    val widget = (treeItem as HierarchyItem).widget

                    setup()

                    val box = checkbox {
                        selectedProperty().bindBidirectional(widget.selected)

                        action {
                            //Widget select everything highlighted in hierarchy
                            val selected = treeView.selectionModel.selectedItems
                                    .filterIsInstance<HierarchyItem>()
                                    .filter { it.widget != widget }
                                    .map { it.widget }
                                    .toMutableList()

                            selected.forEach { it.setSelected(isSelected, false) }
                            selected.add(widget)
                            if(isSelected)
                                WidgetsController.selection.addAll(selected)
                            else
                                WidgetsController.selection.removeAll(selected)


                            //Hierarchy highlight if not already
                            /*if (treeItem != null) {
                                val index = treeView.selectionModel.selectedItems.indexOf(treeItem)
                                if (index == -1) {
                                    treeView.selectionModel.clearSelection()
                                    treeView.selectionModel.select(treeItem)
                                }
                            }*/
                        }

                        selectedProperty().addListener { _, _, newValue ->
                            widget.updateSelection = true
                            //Deselect checkbox if locked
                            if (newValue && widget.isLocked())
                                isSelected = false
                        }
                    }

                    val visibilityBox = checkbox {
                        selectedProperty().bindBidirectional(widget.invisible)

                        //Lock if invisible or unlock if visible and that setting is enabled
                        fun updateLock(widget: Widget, value: Boolean) {
                            if (!widget.isLocked() && (value || Settings.getBoolean(Settings.DISABLE_LOCK_ON_UNHIDDEN))) {
                                widget.setLocked(!widget.isLocked())
                                if (widget.isLocked())
                                    widget.setSelected(false)
                            }
                        }

                        action {
                            //Hide all highlighted
                            treeView.selectionModel.selectedItems
                                    .filterIsInstance<HierarchyItem>()
                                    .filter { it.widget != widget }
                                    .forEach {
                                        it.widget.setInvisible(isSelected)
                                        updateLock(it.widget, isSelected)
                                    }

                            //Hierarchy highlight if not already
                            /*if (treeItem != null) {
                                val index = treeView.selectionModel.selectedItems.indexOf(treeItem)
                                if (index == -1) {
                                    treeView.selectionModel.clearSelection()
                                    treeView.selectionModel.select(treeItem)
                                }
                            }*/
                        }

                        selectedProperty().addListener { _, _, newValue ->
                            updateLock(widget, newValue)
                        }
                    }

                    val lockBox = checkbox {
                        selectedProperty().bindBidirectional(widget.locked)

                        action {
                            //Lock all highlighted
                            treeView.selectionModel.selectedItems
                                    .filterIsInstance<HierarchyItem>()
                                    .filter { it.widget != widget }
                                    .forEach {
                                        it.widget.setLocked(isSelected)
                                        //Deselect if locked
                                        if (isSelected)
                                            it.widget.setSelected(false)
                                        //TODO finish (if invisibility is set then it fires the other listener changing some weird stuff)
//                                        if(it.widget.isInvisible())
//                                            it.widget.setInvisible(false)
                                    }

                            //Hierarchy highlight if not already
                            /*if (treeItem != null) {
                                val index = treeView.selectionModel.selectedItems.indexOf(treeItem)
                                if (index == -1) {
                                    treeView.selectionModel.clearSelection()
                                    treeView.selectionModel.select(treeItem)
                                }
                            }*/
                        }

                        selectedProperty().addListener { _, _, newValue ->
                            //Deselect if locked
                            if (newValue)
                                widget.setSelected(false)
//                            if(widget.isInvisible())
//                                widget.setInvisible(false)
                        }
                    }

                    cellBox.children.addAll(box, label, space, visibilityBox, lockBox)
                }
            }

            graphic = cellBox
        } else {
            graphic = null
        }
    }
}