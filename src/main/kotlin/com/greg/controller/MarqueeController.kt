package com.greg.controller

import com.greg.Utils
import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.Widget
import javafx.event.EventTarget
import javafx.geometry.BoundingBox
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class MarqueeController(private val widgets: WidgetsController, private var canvas: PannableCanvas) {
    private var marquee = Marquee()
    private var target: EventTarget? = null
    private var widget: Widget? = null

    fun init(event: MouseEvent) {
        target = event.target
        this.widget = widgets.getWidget(event.target)
    }

    fun select(event: MouseEvent) {
        if (marquee.selecting)
            selectContents(event)

        marquee.selecting = false
    }

    fun handle(event: MouseEvent): Boolean {
        //If marquee box isn't already on the screen and...
        //If clicking blank space or a unselected shape with a multi select key down
        if (!marquee.selecting && (target !is Shape || (widget != null && !widget!!.isSelected() && event.isControlDown))) {
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
        val pane = event.source as? Pane ?: return

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
        val pane = event.source as? Pane ?: return

        //Bounds of the canvas
        val bounds = pane.localToScene(pane.layoutBounds)

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
        val pane = event.source as? Pane ?: return

        widgets.getAll()
                .filter {widget ->
                    val widgetBounds = BoundingBox(canvas.boundsInParent.minX + widget.getX().toDouble(), canvas.boundsInParent.minY + widget.getY().toDouble(), widget.getWidth().toDouble(), widget.getHeight().toDouble())
                    marquee.boundsInParent.intersects(widgetBounds)
                }
                .forEach { widget ->
                    if (event.isControlDown)
                        widget.setSelected(!widget.isSelected())
                    else
                        widget.setSelected(true)
                }

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        pane.children.remove(marquee)

        event.consume()
    }
}