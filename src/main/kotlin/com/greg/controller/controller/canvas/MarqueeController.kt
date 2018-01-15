package com.greg.controller.controller.canvas

import com.greg.Utils
import com.greg.controller.controller.WidgetsController
import com.greg.controller.model.Widget
import com.greg.controller.view.WidgetShape
import com.greg.ui.canvas.state.states.normal.selection.marquee.Marquee
import javafx.event.EventTarget
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape

class MarqueeController(private val widgets: WidgetsController, private val pane: Pane) {
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
        marquee.draw(Utils.constrain(event.x, bounds.width), Utils.constrain(event.y, bounds.height))

        event.consume()
    }

    /**
     * Adds all shapes within the marquee box to the selection model
     * @param event
     */
    private fun selectContents(event: MouseEvent) {
        //Add everything in box to selection
        pane.children
                .filterIsInstance<WidgetShape>()
                .filter { it.boundsInParent.intersects(marquee.boundsInParent) }
                .mapNotNull { widgets.getWidget(it) }
                .forEach {
                    if (event.isControlDown) {
                        it.setSelected(!it.isSelected())
                    } else {
                        it.setSelected(true)
                    }
                }

        //Reset marquee
        marquee.reset()

        //Remove from widgetCanvas
        pane.children.remove(marquee)

        event.consume()
    }
}