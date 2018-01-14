package com.greg.ui.hierarchy

import com.greg.controller.ControllerView
import com.greg.ui.panel.panels.element.elements.SpaceElement
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.scene.layout.HBox
import tornadofx.action

class CustomTreeCell(private val tree: TreeView<String>, controller: ControllerView) : TreeCell<String>() {

    init {
        selectedProperty().addListener { _, _, _ ->
            if(treeItem is CustomTreeItem && (treeItem as CustomTreeItem).widget.locked)
                updateSelected(false)
        }
    }
    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        // If the cell is empty we don't show anything.
        if (isEmpty) {
            graphic = null
            text = null
        } else {
            // We only show the custom cell if it is a leaf, meaning it has
            // no children.
            if (this.treeItem.isLeaf && item != "Canvas" && treeItem is CustomTreeItem) {

                val item = treeItem as CustomTreeItem
                // A custom HBox that will contain your check box, label and
                // button.
                val cellBox = HBox(10.0)
                val checkBox = CheckBox()
                checkBox.isSelected = item.widget.locked
                val label = Label(treeItem.value)
                checkBox.action {
                    item.widget.locked = checkBox.isSelected
                    item.widget.selected = false
                }

                cellBox.children.addAll(label, SpaceElement(), checkBox)

                // We set the cellBox as the graphic of the cell.
                graphic = cellBox
                text = null
            } else {
                // If this is the root we just display the text.
                graphic = null
                text = item
            }
        }
    }
}