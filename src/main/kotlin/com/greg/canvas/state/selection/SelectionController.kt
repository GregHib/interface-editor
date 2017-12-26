package com.greg.canvas.state.selection

import com.greg.Utils.Companion.constrain
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.edit.EditController
import com.greg.canvas.state.selection.marquee.Marquee
import com.greg.canvas.widget.*
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.event.EventTarget
import javafx.scene.input.*
import javafx.scene.shape.Shape


class SelectionController(override var canvas: WidgetCanvas) : PaneController {
    //TODO I think this can still be split down into more classes

    private var marquee = Marquee()
    private var target: EventTarget? = null
    private var widget: Widget? = null

    override fun handleMousePress(event: MouseEvent) {
        //Get the parent widget (can be null)
        val widget = getWidget(event.target)

        target = event.target
        this.widget = widget

        //If clicked something other than a widget
        var selected = widget == null

        if (widget != null && !selected) {
            //or clicked a shape which isn't selected
            selected = !canvas.selectionGroup.contains(widget)
        }

        //Clear current selection
        if (!isMultiSelect(event) && selected)
            canvas.selectionGroup.clear()

        //Always toggle the shape clicked
        if (widget != null)
            handleShape(widget, event)

        initPreDrag(event)
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            //If marquee box isn't already on the screen and...
            //If clicking blank space or a unselected shape with a multi select key down
            if (!marquee.selecting && (target !is Shape || (!canvas.selectionGroup.contains(widget as Widget) && isMultiSelect(event)))) {
                //Begin marquee selection box
                marquee.selecting = true
                addMarqueeBox(event)
            }

            //Transform marquee box or selected shapes to match mouse position
            if (marquee.selecting) {
                drawMarqueeBox(event)
            } else {
                dragSelection(event)
            }
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        if (marquee.selecting)
            selectContents(event)

        marquee.selecting = false
    }

    override fun handleDoubleClick(event: MouseEvent) {
        val widget = getWidget(event.target)
        if (widget != null)
            canvas.controller = EditController(canvas, widget)
    }

    override fun handleMouseClick(event: MouseEvent) {
        canvas.canvasPane.requestFocus()
    }

    private var moveHorizontal = 0.0
    private var moveVertical = 0.0

    override fun handleKeyPress(event: KeyEvent) {
        when (event.code) {
            KeyCode.RIGHT -> moveHorizontal = 1.0
            KeyCode.LEFT -> moveHorizontal = -1.0
            KeyCode.UP -> moveVertical = -1.0
            KeyCode.DOWN -> moveVertical = 1.0
            KeyCode.C -> {
            }
            else -> {
            }
        }

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.X -> {
                    copySelection(event)
                    deleteSelection()
                }
                KeyCode.C -> {
                    copySelection(event)
                }
                KeyCode.V -> {
                    pasteSelection(event)
                }
                else -> {
                }
            }
        }
        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            moveSelection(event, moveHorizontal, moveVertical)
    }

    private fun isWidget(name: String): Boolean {
        return when (name) {
            WidgetText::class.simpleName -> true
            WidgetRectangle::class.simpleName -> true
            Widget::class.simpleName -> true
            else -> false
        }
    }

    private fun pasteSelection(event: KeyEvent) {
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
        canvas.selectionGroup.clear()


        //For each line
        for (line in lines) {
            //If is a valid widget name
            if (isWidget(line)) {
                //Create widget of the corresponding type
                val widget = WidgetBuilder(canvas, line).build()
                canvas.canvasPane.children.add(widget)
                canvas.selectionGroup.add(widget)

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

        //Stops the key event here
        event.consume()
    }

    private fun copySelection(event: KeyEvent) {
        val clipboard = Clipboard.getSystemClipboard()

        //Convert selected widget's into a string of attribute values
        var string = ""
        canvas.selectionGroup.getGroup().forEach { widget ->
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

        //Stops the key event here
        event.consume()
    }

    private fun moveSelection(event: KeyEvent, x: Double, y: Double) {
        canvas.selectionGroup.getGroup().forEach { widget ->
            move(widget, if (event.isShiftDown) x * 10.0 else x, if (event.isShiftDown) y * 10.0 else y)
        }

        //Stops the key event here
        event.consume()
    }

    private fun deleteSelection() {
        canvas.selectionGroup.getGroup().forEach { widget ->
            val success = canvas.canvasPane.children.remove(widget)
            if (!success)
                println("Error deleting widget")
        }
        canvas.selectionGroup.clear()
    }

    override fun handleKeyRelease(event: KeyEvent) {
        when (event.code) {
            KeyCode.RIGHT, KeyCode.LEFT -> moveHorizontal = 0.0
            KeyCode.UP, KeyCode.DOWN -> moveVertical = 0.0
            KeyCode.DELETE -> {
                deleteSelection()
                event.consume()
            }
            else -> {
            }
        }
    }

    /**
     * Drag handling
     */
    private fun initPreDrag(event: MouseEvent) {
        //If has items selected
        if (canvas.selectionGroup.size() > 0) {
            //Set info needed for drag just in case dragging occurs
            canvas.selectionGroup.getGroup().forEach { widget ->
                //save the offset of the shapes position relative to the mouse click
                setWidgetDrag(widget, event, canvas)
            }
        }
    }

    private fun dragSelection(event: MouseEvent) {
        val widgetTarget = getWidget(event.target)
        if (widgetTarget != null && canvas.selectionGroup.contains(widgetTarget)) {
            canvas.selectionGroup.getGroup().forEach { widget ->
                //Move
                moveInCanvas(widget, event)
            }
        }
    }

    /**
     * Marquee handling
     */

    private fun addMarqueeBox(event: MouseEvent) {
        //Remove any existing boxes as only 1 can exist on screen at a time
        if (canvas.canvasPane.children.contains(marquee))
            canvas.canvasPane.children.remove(marquee)

        //create a marquee box
        marquee.add(event.x, event.y)

        //add to the widgetCanvas
        canvas.canvasPane.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {

        //Bounds of the canvas
        val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)

        //draw at that position capping to canvas borders
        marquee.draw(constrain(event.x, bounds.width), constrain(event.y, bounds.height))

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent) {
        //Add everything in box to selection
        canvas.canvasPane.children
                .filter {
                    it is Widget && it.boundsInParent.intersects(marquee.boundsInParent)
                }
                .forEach {
                    handleShape(it as Widget, event)
                }

        //Refresh
        canvas.refreshSelection()

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        canvas.canvasPane.children.remove(marquee)

        event.consume()
    }

    private fun handleShape(widget: Widget, event: MouseEvent) {
        if (event.isControlDown) {
            canvas.selectionGroup.toggle(widget)
        } else {
            canvas.selectionGroup.add(widget)
        }
    }


    /**
     * Convenience functions
     */

    private fun isMultiSelect(event: MouseEvent): Boolean {
        return event.isShiftDown || event.isControlDown
    }

    private fun getWidget(target: EventTarget?): Widget? {
        if (target is Shape) {
            val parent = target.parent
            if (parent is Widget)
                return parent
        }

        return null
    }
}