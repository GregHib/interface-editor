package com.greg.controller.selection

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.widgets.WidgetType
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

        val widgetMap = mutableMapOf<Widget, WidgetData>()

        //For each line
        for (line in lines) {

            //TODO serialize w/children
        }

        //Display all widgets at once
        widgets.addAll(widgetMap.keys.toTypedArray())

        //Apply data & selections
        widgetMap.forEach { widget, data ->
            widget.fromData(data)
            if (!widget.isSelected())
                widget.setSelected(true, false)
        }

        //Select all widgets
        WidgetsController.selection.addAll(widgetMap.keys)
    }

    fun copy() {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        val array = widgets.getSelection().map { it.toJson() }


        //Set the clipboard
        if (array.isNotEmpty()) {
            val content = ClipboardContent()
            content.putString(array.joinToString("\n"))
            clipboard.setContent(content)
        }
    }

    fun clone() {
        //TODO fix cloning children in containers
        val list = widgets.get().filter { it.isSelected() }

        WidgetsController.selection.removeAll(list)

        val clones = list.map { widget ->
            val clone = WidgetDataConverter.create(widget.toData())
            widget.setSelected(false, false)
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