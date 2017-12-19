package com.greg.canvas.state.selection

import com.greg.Utils.Companion.constrain
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.edit.EditController
import com.greg.canvas.state.selection.marquee.Marquee
import com.greg.canvas.widget.Widget
import javafx.event.EventTarget
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
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
        if(widget != null)
            canvas.controller = EditController(canvas, widget)
    }

    override fun handleMouseClick(event: MouseEvent) {
        canvas.canvasPane.requestFocus()
    }

    private var moveHorizontal = 0.0
    private var moveVertical = 0.0

    override fun handleKeyPress(event: KeyEvent) {
        when(event.code) {
            KeyCode.RIGHT -> moveHorizontal = 1.0
            KeyCode.LEFT -> moveHorizontal = -1.0
            KeyCode.UP -> moveVertical = -1.0
            KeyCode.DOWN -> moveVertical = 1.0
            else -> {
            }
        }
        moveSelection(event,  moveHorizontal, moveVertical)
    }

    private fun moveSelection(event: KeyEvent, x: Double, y: Double) {
        canvas.selectionGroup.getGroup().forEach { widget ->
            move(widget, if(event.isShiftDown) x * 10.0 else x, if(event.isShiftDown) y * 10.0 else y)
        }

        //Stops the key event here
        event.consume()
    }

    private fun deleteSelection() {
        canvas.selectionGroup.getGroup().forEach { widget ->
            val success = canvas.canvasPane.children.remove(widget)
            if(!success)
                println("Error deleting widget")
        }
        canvas.selectionGroup.clear()
    }

    override fun handleKeyRelease(event: KeyEvent) {
        when(event.code) {
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