package com.greg.ui.canvas.state.states.normal.selection

import com.greg.ui.actions.memento.MementoBuilder
import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.widget.builder.WidgetMementoBuilderAdapter
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.Pane

class Interaction(private val selection: Selection, private val pane: Pane) {

    fun paste() {
        val clipboard = Clipboard.getSystemClipboard()
        val string = clipboard.string

        //Check clipboard isn't empty
        if (string.isNullOrEmpty())
            return

        //Split clipboard into lines
        val lines = string.split("\n")

        //Clear the current selection
        selection.clear()

        //For each line
        for (line in lines) {
            //Extract name and list from string
            val name = line.substring(0, line.indexOf("["))
            val data = line.substring(line.indexOf("["), line.length)
            val list = data.replaceFirst("[", "").replace("]", "").split(", ")

            //If is a valid widget name
            if (isWidget(name)) {
                //Create a memento using data
                val builder = MementoBuilder()
                builder.setName(name)
                val memento = builder.build()
                memento.values.addAll(list)

                //Create widget of the corresponding type
                val widget = WidgetMementoBuilderAdapter(memento).build()
                pane.children.add(widget)
                selection.add(widget)
            } else {
                error("Error processing paste line: $line")
            }
        }
    }

    fun copy() {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        var string = ""
        selection.get().forEach { widget ->
            string += "${widget.getMemento()}\n"
        }

        //Set the clipboard
        val content = ClipboardContent()
        content.putString(string.substring(0, string.length - 1))//Remove the extra line space
        clipboard.setContent(content)
    }

    fun clone() {
        val selected = mutableListOf<WidgetGroup>()
        selected.addAll(selection.get())

        //Clear the current selection
        selection.clear()

        //Clone all selected widgets and attributes
        selected.forEach { widget ->
            val clone = WidgetMementoBuilderAdapter(widget.getMemento()).build()
            pane.children.add(clone)
            clone.toFront()
            clone.start = widget.start
            selection.add(clone)
        }
    }

    private fun isWidget(name: String): Boolean {
        return WidgetType.forString(name) != null
    }
}