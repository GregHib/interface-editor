package com.greg.canvas.state.selection.marquee

import com.greg.Utils
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.selection.WidgetSelection
import com.greg.canvas.widget.Widget
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class MarqueeController(private var canvas: WidgetCanvas, private val canvasPane: Pane, private val selection: WidgetSelection) {

    private var marquee = Marquee()
    private var target: EventTarget? = null
    private var widget: Widget? = null

    private fun addMarqueeBox(event: MouseEvent) {
        //Remove any existing boxes as only 1 can exist on screen at a time
        if (canvasPane.children.contains(marquee))
            canvasPane.children.remove(marquee)

        //create a marquee box
        marquee.add(event.x, event.y)

        //add to the widgetCanvas
        canvasPane.children.add(marquee)

        event.consume()
    }

    private fun drawMarqueeBox(event: MouseEvent) {

        //Bounds of the canvas
        val bounds = canvasPane.localToScene(canvasPane.layoutBounds)

        //draw at that position capping to canvas borders
        marquee.draw(Utils.constrain(event.x, bounds.width), Utils.constrain(event.y, bounds.height))

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent) {
        //Add everything in box to selection
        canvasPane.children
                .filter {
                    it is Widget && it.boundsInParent.intersects(marquee.boundsInParent)
                }
                .forEach {
                    selection.handle(it as Widget, event)
                }

        //Refresh
        canvas.refreshSelection()

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        canvasPane.children.remove(marquee)

        event.consume()
    }

    fun handleSelecting(event: MouseEvent): Boolean {
        //If marquee box isn't already on the screen and...
        //If clicking blank space or a unselected shape with a multi select key down
        if (!marquee.selecting && (target !is Shape || (!canvas.selectionGroup.contains(widget as Widget) && event.isControlDown))) {
            //Begin marquee selection box
            marquee.selecting = true
            addMarqueeBox(event)
        }

        //Transform marquee box to match mouse position
        if (marquee.selecting) {
            drawMarqueeBox(event)
            return true
        }

        return false
    }

    fun select(event: MouseEvent) {
        if (marquee.selecting)
            selectContents(event)

        marquee.selecting = false
    }

    fun init(event: MouseEvent, widget: Widget?) {
        target = event.target
        this.widget = widget
    }
}