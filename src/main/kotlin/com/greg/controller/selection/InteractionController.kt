package com.greg.controller.selection

import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.memento.Memento
import com.greg.model.widgets.memento.MementoString
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

class InteractionController(val widgets: WidgetsController) {

    private fun createWidget(container: String): Widget {
        val name = MementoString.getType(container)
        val list = MementoString.getVariables(container)

        //Create a memento using data
        val memento = Memento(WidgetType.valueOf(name))
        println("Create widget: $list")//TODO fix list containing $'s? not children?
        memento.addAll(list)//TODO what if memento has children?

        //Create widget of the corresponding type
        val widget = WidgetBuilder(memento.type).build()

        widget.restore(memento)

        return widget
    }

    private fun createChildren(index: Int, containers: ArrayList<String>): Widget {
        val container = containers[index]
        val values = MementoString.getVariables(container)

        //create widget
        val widget = createWidget(container)

        if (widget is WidgetContainer) {
            widget.setChildren(values
                    .filter { it.startsWith("\$") }
                    .map {
                        val childIndex = it.substring(1).toInt()
                        createChildren(childIndex, containers)
                    }.toMutableList()
            )
        }
        /*
        for every index in the array

        get container using index

        get container values

        create widget

        for every value

        if value is an index {
            call this method (returns children)

            add children to widget
        }

        return widget


         */
        return widget
    }

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

            val name = MementoString.getType(line)

            if (!isWidget(name))
                error("Error processing paste line: $line")

            val type = WidgetType.valueOf(name)

            if (type == WidgetType.CONTAINER && MementoString.hasArray(line)) {
                val array = MementoString.extractArray(line)

                val containers = MementoString.extractContainers(array)

                val final = MementoString.replaceContainers(array, containers)

                val list = MementoString.getVariables(line)

                val children = final.split(", ").map { createChildren(it.substring(1).toInt(), containers) }

                //Create a memento using data
                val memento = Memento(type)
                memento.addAll(list)

                //Create widget of the corresponding type
                val widget = WidgetBuilder(memento.type).build()

                (widget as? WidgetContainer)?.setChildren(children.toMutableList())

                //Add to the list
                widgetMap[widget] = memento
            } else if (MementoString.hasFormat(line)) {
                val list = MementoString.getVariables(line)

                //Create a memento using data
                val memento = Memento(WidgetType.valueOf(name))
                memento.addAll(list)

                //Create widget of the corresponding type
                val widget = WidgetBuilder(memento.type).build()

                //Add to the list
                widgetMap[widget] = memento
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
        val list = widgets.get().filter { it.isSelected() }

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