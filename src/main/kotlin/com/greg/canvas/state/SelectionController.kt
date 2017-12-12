package com.greg.canvas.state

import com.greg.Utils.Companion.constrain
import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.marquee.Marquee
import com.greg.canvas.widget.Widget
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape

class SelectionController(var canvas: WidgetCanvas) : PaneController {

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
    }

    override fun handleMouseClick(event: MouseEvent) {
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
                val offsetX = canvas.canvasPane.localToScene(widget.getNode().boundsInParent).minX - event.sceneX
                val offsetY = canvas.canvasPane.localToScene(widget.getNode().boundsInParent).minY - event.sceneY
                widget.drag = DragModel(offsetX, offsetY)
            }
        }
    }

    private fun dragSelection(event: MouseEvent) {
        val widgetTarget = getWidget(event.target)
        if (widgetTarget != null && canvas.selectionGroup.contains(widgetTarget)) {
            canvas.selectionGroup.getGroup().forEach { widget ->

                //Bounds of the container
                val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)//TODO what's the difference between this and getCanvasX/Y

                //The actual positioning of the shape relative to the container
                var x = event.sceneX - bounds.minX + widget.drag!!.offsetX!!
                var y = event.sceneY - bounds.minY + widget.drag!!.offsetY!!

                //Size of shape
                val width = widget.getNode().layoutBounds.width
                val height = widget.getNode().layoutBounds.height

                //Constrain position to within the container
                x = constrain(x, bounds.width - width)
                y = constrain(y, bounds.height - height)

                //Move
                widget.getNode().relocate(x, y)

                //Display in front
                widget.getNode().toFront()

                //Refresh
                canvas.refreshPosition()
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

        //calculate the x,y within the widgetCanvas
        val x = getCanvasX(event)
        val y = getCanvasY(event)

        //create a marquee box
        marquee.add(x, y)

        //add to the widgetCanvas
        canvas.canvasPane.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {
        //get x, y local to widgetCanvas
        var x = getCanvasX(event)
        var y = getCanvasY(event)

        val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)

        val width = bounds.width
        val height = bounds.height

        //Cap to widgetCanvas size
        x = constrain(x, width)
        y = constrain(y, height)

        //draw at that position
        marquee.draw(x, y)

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

    private fun getCanvasX(event: MouseEvent): Double {
        return event.sceneX - canvas.canvasPane.localToScene(canvas.canvasPane.boundsInLocal).minX
    }

    private fun getCanvasY(event: MouseEvent): Double {
        return event.sceneY - canvas.canvasPane.localToScene(canvas.canvasPane.boundsInLocal).minY
    }

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