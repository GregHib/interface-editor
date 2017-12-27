package com.greg.ui.canvas.state.states.normal.selection.marquee

import com.greg.Utils.Companion.constrain
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class MarqueeFacade(private val pane: Pane) {

    private var marquee = Marquee()
    private var target: EventTarget? = null
    private var widget: WidgetGroup? = null

    fun init(event: MouseEvent, widget: WidgetGroup?) {
        target = event.target
        this.widget = widget
    }

    fun select(event: MouseEvent, selection: Selection, canvas: Canvas) {
        if (marquee.selecting)
            selectContents(event, selection, canvas)

        marquee.selecting = false
    }

    fun handle(event: MouseEvent, selection: Selection): Boolean {
        //If marquee box isn't already on the screen and...
        //If clicking blank space or a unselected shape with a multi select key down
        if (!marquee.selecting && (target !is Shape || (widget != null && !selection.contains(widget!!) && event.isControlDown))) {
            //Begin marquee selection box
            marquee.selecting = true
            add(event)
        }

        //Transform marquee box to match mouse position
        if (marquee.selecting) {
            draw(event)
            return true
        }

        return false
    }

    private fun add(event: MouseEvent) {
        //Remove any existing boxes as only 1 can exist on screen at a time
        if (pane.children.contains(marquee))
            pane.children.remove(marquee)

        //create a marquee box
        marquee.add(event.x, event.y)

        //add to the widgetCanvas
        pane.children.add(marquee)

        event.consume()
    }

    private fun draw(event: MouseEvent) {

        //Bounds of the canvas
        val bounds = pane.localToScene(pane.layoutBounds)

        //draw at that position capping to canvas borders
        marquee.draw(constrain(event.x, bounds.width), constrain(event.y, bounds.height))

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent, selection: Selection, canvas: Canvas) {
        //Add everything in box to selection
        pane.children
                .filter {
                    it is WidgetGroup && it.boundsInParent.intersects(marquee.boundsInParent)
                }
                .forEach {
                    selection.handle(it as WidgetGroup, event)
                }

        //Refresh
        canvas.refreshSelection()

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        pane.children.remove(marquee)

        event.consume()
    }
}