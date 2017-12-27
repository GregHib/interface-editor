package com.greg.canvas.state.selection

import com.greg.canvas.widget.Widget
import com.greg.canvas.widget.WidgetBuilder
import com.greg.canvas.widget.WidgetInterface
import com.greg.canvas.widget.types.WidgetType
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.Pane

class InteractionController(private val selectionGroup: SelectionGroup, private val canvasPane: Pane) {
    private fun isWidget(name: String): Boolean {
        return WidgetType.forString(name) != null
    }

    fun paste() {
        val clipboard = Clipboard.getSystemClipboard()
        val string = clipboard.string

        //Check clipboard isn't empty & can be split
        if(string.isNullOrEmpty() || !string.contains("\n"))
            return

        //Split clipboard into lines
        val lines = string.split("\n")

        //Check the first line is a valid widget type
        if (!isWidget(lines.first()))
            return

        //Begin the widget creation
        var index = 0
        val attributes = mutableListOf<Attribute>()
        val components = mutableListOf<WidgetInterface>()

        //Clear the current selection
        selectionGroup.clear()


        //For each line
        for (line in lines) {
            //If is a valid widget name
            if (isWidget(line)) {
                //Create widget of the corresponding type
                val widget = WidgetBuilder(WidgetType.forString(line)).build()
                canvasPane.children.add(widget)
                selectionGroup.add(widget)

                //Reset all of the reused values
                index = 0
                attributes.clear()
                components.clear()

                //Map all of the attributes for this widget type
                for (component in widget.components.reversed()) {
                    AttributePaneType.values()
                            .mapNotNull { component.getAttributes(it) }
                            .filter { it.isNotEmpty() }
                            .flatMap { it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title })) }
                            .forEach {
                                attributes.add(it)
                                components.add(component)
                            }
                }
            } else {
                //Otherwise it's an attribute

                //If it's not out of bounds
                if(index >= attributes.size)
                    return

                //Set the attribute value of the cloned widget to the one in the paste
                attributes[index].setValue(components[index], attributes[index].type.convert(line))
                index++
            }
        }
    }

    fun copy() {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        var string = ""
        selectionGroup.getGroup().forEach { widget ->
            string += "${widget.components.reversed().first()::class.simpleName}\n"
            for (component in widget.components.reversed()) {
                AttributePaneType.values()
                        .mapNotNull { component.getAttributes(it) }
                        .filter { it.isNotEmpty() }
                        .flatMap { it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title })) }
                        .forEach { string += "${it.getValue(component)}\n" }
            }
        }

        //Set the clipboard
        val content = ClipboardContent()
        content.putString(string.substring(0, string.length - 1))//Remove the extra line space
        clipboard.setContent(content)
    }

    fun clone() {
        val selected = mutableListOf<Widget>()
        selected.addAll(selectionGroup.getGroup())

        //Clear the current selection
        selectionGroup.clear()

        //Clone all selected widgets and attributes
        selected.forEach { widget ->
            val clone = WidgetBuilder(WidgetType.forString(widget.components.reversed().first()::class.simpleName)).build()
            canvasPane.children.add(clone)
            clone.toFront()
            clone.drag = widget.drag
            selectionGroup.add(clone)

            for (component in widget.components.reversed()) {
                clone.components
                        .filter { component::class.simpleName == it::class.simpleName }
                        .forEach { clonedComponent ->
                            for (type in  AttributePaneType.values()) {
                                val attributes = component.getAttributes(type)?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title }))
                                val clonedAttributes = clonedComponent.getAttributes(type)?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title }))
                                if(attributes != null && clonedAttributes != null) {
                                    for ((index, attribute) in attributes.withIndex()) {
                                        clonedAttributes[index].setValue(clonedComponent, attribute.getValue(component))
                                    }
                                }
                            }
                        }
            }
        }
    }

}