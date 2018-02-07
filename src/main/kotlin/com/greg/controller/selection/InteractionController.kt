package com.greg.controller.selection

import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.memento.Memento
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

        //For each line
        for (line in lines) {
            //Extract name and list from string
            if(!line.contains("[") || !line.contains("]"))
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
                widgets.add(widget)
                widget.restore(memento)
                if(!widget.isSelected())
                    widget.setSelected(true)
            } else {
                error("Error processing paste line: $line")
            }
        }
    }

    fun copy() {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        var string = ""
        widgets.forSelected { widget ->
            string += "${widget.getMemento()}\n"
        }

        //Set the clipboard
        val content = ClipboardContent()
        content.putString(string.substring(0, string.length - 1))//Remove the extra line space
        clipboard.setContent(content)
    }

    fun clone() {
        //TODO can be done better with iteration not a new list
        val selected = mutableListOf<Widget>()
        selected.addAll(widgets.getAll())

        selected.forEach { widget ->
            if(widget.isSelected()) {
                val memento = widget.getMemento()
                val clone = WidgetBuilder(memento.type).build()
                widgets.add(clone)
                widget.setSelected(false)
                clone.restore(memento)
                if(!clone.isSelected())
                    clone.setSelected(true)
            }
        }
    }

    private fun isWidget(name: String): Boolean {
        return WidgetType.forString(name) != null
    }
}