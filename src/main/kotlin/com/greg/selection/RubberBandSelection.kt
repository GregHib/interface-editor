package com.greg.selection

import com.greg.widget.Widget
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class RubberBandSelection(private var group: Pane, private var selectionModel: SelectionModel) {

    private var marquee = Marquee()
    private var target: EventTarget? = null
    private var widget: Widget? = null

    /**
     * Mouse events
     */
    private var onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        target = event.target
        val widget = getWidget(target)
        this.widget = widget

        //If clicked something other than a widget
        var selected = widget == null

        if (widget != null && !selected) {
            //or clicked a shape which isn't selected
            selected = !selectionModel.contains(widget)
        }

        //Clear current selection
        if (!isMultiSelect(event) && selected)
            selectionModel.clear()


        if(widget != null) {
            //Always toggle the shape clicked
            handleShape(widget, event)
        }

        initPreDrag(event)
    }

    private var onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (event.isPrimaryButtonDown) {
            //If marquee box isn't already on the screen and...
            //If clicking blank space or a unselected shape with a multi select key down
            if (!marquee.selecting && (target !is Shape || (!selectionModel.contains(widget as Widget) && isMultiSelect(event)))) {
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

    private var onMouseReleasedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (marquee.selecting)
            selectContents(event)

        marquee.selecting = false
    }

    init {
        group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler)
        group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler)
        group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler)
    }

    /**
     * Drag handling
     */
    private fun initPreDrag(event: MouseEvent) {
        //If has items selected
        if (selectionModel.size() > 0) {
            //Set info needed for drag just in case dragging occurs
            selectionModel.getSelection().forEach { n ->
                //save the offset of the shapes position relative to the mouse click
                var offsetX = group.localToScene(n.boundsInParent).minX - event.sceneX
                var offsetY = group.localToScene(n.boundsInParent).minY - event.sceneY
                n.drag = DragModel(offsetX, offsetY)
            }
        }
    }

    private fun dragSelection(event: MouseEvent) {
        var widget = getWidget(event.target)
        if (widget != null && selectionModel.contains(widget)) {
            selectionModel.getSelection().forEach { n ->
                //Bounds of the container
                val bounds = group.localToScene(group.layoutBounds)

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
        if (group.children.contains(marquee))
            group.children.remove(marquee)

        //calculate the x,y within the pane
        var actualX = event.sceneX - group.localToScene(group.boundsInLocal).minX
        var actualY = event.sceneY - group.localToScene(group.boundsInLocal).minY

        //create a marquee box
        marquee.add(actualX, actualY)

        //add to the pane
        group.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {
        //get x, y local to pane
        var actualX = event.sceneX - group.localToScene(group.boundsInLocal).minX
        var actualY = event.sceneY - group.localToScene(group.boundsInLocal).minY

        val bounds = group.localToScene(group.layoutBounds)

        val width = bounds.width
        val height = bounds.height

        //Cap to pane size
        actualX = constrain(actualX, width)
        actualY = constrain(actualY, height)

        //draw at that position
        marquee.draw(actualX, actualY)

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent) {
        //Add everything in box to selection
        group.children
                .filter { it is Widget && it.boundsInParent.intersects(marquee.boundsInParent) }
                .forEach {
                    handleShape(it as Widget, event)
                }

        //Reset marquee
        marquee.reset()

        //Remove from pane
        group.children.remove(marquee)

        event.consume()
    }

    /**
     * Selection handling
     */

    private fun handleShape(shape: Widget, event: MouseEvent) {
        if (event.isControlDown) {
            toggle(shape)
        } else {
            selectionModel.add(shape)
        }
    }

    private fun toggle(shape: Widget) {
        if (selectionModel.contains(shape)) {
            selectionModel.remove(shape)
        } else {
            selectionModel.add(shape)
        }
    }


    /**
     * Convince functions
     */

    private fun isMultiSelect(event: MouseEvent): Boolean {
        return event.isShiftDown || event.isControlDown
    }

    /**
     * Constrain
     * @param value
     * between 0 and
     * @param max
     */
    private fun constrain(value: Double, max: Double): Double {
        return if (value < 0.0) 0.0 else if (value > max) max else value
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