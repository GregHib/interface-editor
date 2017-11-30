package com.greg.canvas.state

import com.greg.Utils.Methods.constrain
import com.greg.canvas.selection.SelectionGroup
import com.greg.selection.DragModel
import com.greg.selection.Marquee
import com.greg.widget.Widget
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class SelectionController(override var canvas: Pane, private val refresh: Unit) : PaneController {

    private var selectionGroup = SelectionGroup()
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
            selected = !selectionGroup.contains(widget)
        }

        //Clear current selection
        if (!isMultiSelect(event) && selected)
            selectionGroup.clear()


        //Always toggle the shape clicked
        if (widget != null)
            handleShape(widget, event)

        initPreDrag(event)
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            //If marquee box isn't already on the screen and...
            //If clicking blank space or a unselected shape with a multi select key down
            if (!marquee.selecting && (target !is Shape || (!selectionGroup.contains(widget as Widget) && isMultiSelect(event)))) {
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
        if (selectionGroup.size() > 0) {
            //Set info needed for drag just in case dragging occurs
            selectionGroup.getGroup().forEach { n ->
                //save the offset of the shapes position relative to the mouse click
                var offsetX = canvas.localToScene(n.boundsInParent).minX - event.sceneX
                var offsetY = canvas.localToScene(n.boundsInParent).minY - event.sceneY
                n.drag = DragModel(offsetX, offsetY)
            }
        }
    }

    private fun dragSelection(event: MouseEvent) {
        var widget = getWidget(event.target)
        if (widget != null && selectionGroup.contains(widget)) {
            selectionGroup.getGroup().forEach { n ->
                //Bounds of the container
                val bounds = canvas.localToScene(canvas.layoutBounds)

                //The actual positioning of the shape relative to the container
                var actualX = event.sceneX - bounds.minX + n.drag.offsetX!!
                var actualY = event.sceneY - bounds.minY + n.drag.offsetY!!

                //Size of shape
                val width = n.layoutBounds.width
                val height = n.layoutBounds.height

                //Constrain position to within the container
                actualX = constrain(actualX, bounds.width - width)
                actualY = constrain(actualY, bounds.height - height)

                //Move
                n.relocate(actualX, actualY)

                //Display in front
                n.toFront()
            }
        }
    }

    /**
     * Marquee handling
     */

    private fun addMarqueeBox(event: MouseEvent) {
        //Remove any existing boxes as only 1 can exist on screen at a time
        if (canvas.children.contains(marquee))
            canvas.children.remove(marquee)

        //calculate the x,y within the widgetCanvas
        var x = getCanvasX(event)
        var y = getCanvasY(event)

        //create a marquee box
        marquee.add(x, y)

        //add to the widgetCanvas
        canvas.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {
        //get x, y local to widgetCanvas
        var x = getCanvasX(event)
        var y = getCanvasY(event)

        val bounds = canvas.localToScene(canvas.layoutBounds)

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
        canvas.children
                .filter {
                    it is Widget && it.boundsInParent.intersects(marquee.boundsInParent)
                }
                .forEach {
                    handleShape(it as Widget, event)
                }

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        canvas.children.remove(marquee)

        event.consume()
    }

    private fun handleShape(widget: Widget, event: MouseEvent) {
        if (event.isControlDown) {
            selectionGroup.toggle(widget)
        } else {
            selectionGroup.add(widget)
        }

        refresh
    }


    /**
     * Convenience functions
     */

    private fun getCanvasX(event: MouseEvent): Double {
        return event.sceneX - canvas.localToScene(canvas.boundsInLocal).minX
    }

    private fun getCanvasY(event: MouseEvent): Double {
        return event.sceneY - canvas.localToScene(canvas.boundsInLocal).minY
    }

    private fun isMultiSelect(event: MouseEvent): Boolean {
        return event.isShiftDown || event.isControlDown
    }

    private fun getWidget(target: EventTarget?): Widget? {
        if (target is Shape) {
            var parent = target.parent
            if (parent is Widget)
                return parent
        }

        return null
    }
}