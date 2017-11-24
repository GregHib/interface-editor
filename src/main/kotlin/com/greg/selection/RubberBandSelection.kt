package com.greg.selection

import com.greg.widget.Widget
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class RubberBandSelection(private var group: Pane, private var selectionModel: SelectionModel) {

    /**
     * TODO
     * Improve & comment marquee code
     * Remove getX/getY
     *
     * replace etc..
     * actualX = if(actualX < 0.0) 0.0 else if(actualX > bounds.width - width) bounds.width - width else actualX
     *
     * with something like
     * clip(actualX, width)
     */

    private var marquee = Marquee()
    private var start: EventTarget? = null

    /**
     * Get relative mouse position on pane
     */
    private fun getX(event: MouseEvent): Double {
        val x = group.localToScene(group.boundsInLocal).minX
        return if (event.sceneX < x) 0.0 else if (event.sceneX > x + group.width) group.width else event.sceneX - group.localToScene(group.boundsInLocal).minX
    }

    private fun getY(event: MouseEvent): Double {
        val y = group.localToScene(group.boundsInLocal).minY
        return if (event.sceneY < y) 0.0 else if (event.sceneY > y + group.height) group.height else event.sceneY - group.localToScene(group.boundsInLocal).minY
    }


    private fun isMultiSelect(event: MouseEvent): Boolean {
        return event.isShiftDown || event.isControlDown
    }

    /**
     * Drag handling
     */
    private fun initPreDrag(event: MouseEvent) {
        //If has items selected
        if (selectionModel.size() > 0) {
            //Set info needed for drag just in case dragging occurs
            selectionModel.getSelection().forEach { n ->
                if (n is Widget) {
                    //save the offset of the shapes position relative to the mouse click
                    var offsetX = group.localToScene(n.boundsInParent).minX - event.sceneX
                    var offsetY = group.localToScene(n.boundsInParent).minY - event.sceneY
                    n.drag = DragModel(offsetX, offsetY)
                }
            }
        }
    }

    private fun dragSelection(event: MouseEvent) {
        group.layoutBounds
        if (selectionModel.contains(event.target as Shape)) {
            selectionModel.getSelection().forEach { n ->
                if (n is Widget) {

                    //Bounds of the container
                    val bounds = group.localToScene(group.layoutBounds)

                    //The actual positioning of the shape relative to the container
                    var actualX = event.sceneX - bounds.minX + n.drag.offsetX!!
                    var actualY = event.sceneY - bounds.minY + n.drag.offsetY!!

                    //Size of shape
                    val width = n.layoutBounds.width
                    val height = n.layoutBounds.height

                    //Constrain position to within the container
                    actualX = if(actualX < 0.0) 0.0 else if(actualX > bounds.width - width) bounds.width - width else actualX
                    actualY = if(actualY < 0.0) 0.0 else if(actualY > bounds.height - height) bounds.height - height else actualY

                    //Move
                    n.relocate(actualX, actualY)

                    //Display in front
                    n.toFront()
                }
            }
        }
    }

    /**
     * Marquee handling
     */
    private fun addMarqueeBox(event: MouseEvent) {
        if (group.children.contains(marquee))
            group.children.remove(marquee)

        marquee.add(getX(event), getY(event))

        group.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {
        val x = getX(event)
        val y = getY(event)

        marquee.draw(x, y)

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent) {
        group.children
                .filter { it is Widget && it.boundsInParent.intersects(marquee.boundsInParent) }
                .forEach {
                    handleShape(it as Shape, event)
                }

        selectionModel.log()

        marquee.reset()

        group.children.remove(marquee)

        event.consume()
    }

    /**
     * Selection handling
     */
    private fun handleShape(shape: Shape, event: MouseEvent) {
        if (event.isControlDown) {
            toggle(shape)
        } else {
            selectionModel.add(shape)
        }
    }

    private fun toggle(shape: Shape) {
        if (selectionModel.contains(shape)) {
            selectionModel.remove(shape)
        } else {
            selectionModel.add(shape)
        }
    }


    /**
     * Mouse events
     */
    private var onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        start = event.target

        //If clicking empty space or a shape which isn't selected
        var selected = start !is Shape || !selectionModel.contains(start as Shape)

        //Clear current selection
        if (!isMultiSelect(event) && selected)
            selectionModel.clear()

        //Always toggle the shape clicked
        if (start is Shape)
            handleShape(start as Shape, event)

        initPreDrag(event)
    }

    private var onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (event.isPrimaryButtonDown) {
            //If marquee box isn't already on the screen and...
            //If clicking blank space or a unselected shape with a multi select key down
            if (!marquee.selecting && (start !is Shape || (!selectionModel.contains(start as Shape) && isMultiSelect(event)))) {
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
}