package com.greg.controller.selection

import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.memento.Memento
import com.greg.model.widgets.type.Widget
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

class InteractionController(val widgets: WidgetsController) {
    fun paste() {
        val clipboard = Clipboard.getSystemClipboard()
        val string = clipboard.string

        //Check clipboard isn't empty
        if (string.isNullOrEmpty())
            return

        //Split clipboard into lines
        val lines = string.split("\n")

        //Clear the current selection
        widgets.clearSelection()

        val widgetMap = mutableMapOf<Widget, Memento>()

        //For each line
        for (line in lines) {
            //Extract name and list from string
            if (!line.contains("[") || !line.contains("]"))
                continue

            val name = line.substring(0, line.indexOf(" ["))
            val nameless = line.substring(line.indexOf("[") + 1, line.length)
            val data = nameless.substring(0, nameless.lastIndexOf("]"))
            val list = data.split(", ").toMutableList()

            //If is a valid widget name
            if (isWidget(name)) {
                //Create a memento using data
                val memento = Memento(WidgetType.valueOf(name))
                memento.addAll(list)

                //Create widget of the corresponding type
                val widget = WidgetBuilder(memento.type).build()

                //Add to the list
                widgetMap[widget] = memento
            } else {
                error("Error processing paste line: $line")
            }
        }

        //Display all widgets at once
        widgets.addAll(widgetMap.keys.toTypedArray())

        //Apply memento & selections
        widgetMap.forEach { widget, memento ->
            widget.restore(memento)
            if (!widget.isSelected())
                widget.setSelected(true, false)
        }

        //Select all widgets
        WidgetsController.selection.addAll(widgetMap.keys)
    }

    fun copy() {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        val array = widgets.getSelection().map { it.getMemento().toString() }

        //Set the clipboard
        if (array.isNotEmpty()) {
            val content = ClipboardContent()
            content.putString(array.joinToString("\n"))
            clipboard.setContent(content)
        }
    }

    fun clone() {
        val list = widgets.getAll().filter { it.isSelected() }

        WidgetsController.selection.removeAll(list)

        val clones = list.map { widget ->
            val memento = widget.getMemento()
            val clone = WidgetBuilder(memento.type).build()
            widget.setSelected(false, false)
            clone.restore(memento)
            clone.setSelected(true, false)
            clone
        }

        WidgetsController.selection.addAll(clones)
        widgets.addAll(clones.toTypedArray())
    }

    private fun isWidget(name: String): Boolean {
        return WidgetType.forString(name) != null
    }
}