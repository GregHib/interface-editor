package com.greg.controller.canvas

import com.greg.controller.widgets.WidgetsController
import com.greg.view.canvas.widgets.WidgetShape
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
class NodeGestures(val widgets: WidgetsController) {

    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown || event.target !is Shape)
            return@EventHandler

        //Return if node selected isn't on canvas
        val pane = getCanvas(event.source) as? PannableCanvas ?: return@EventHandler

        //For all nodes selected (including this one)
        widgets.forSelected { widget ->
            //Store distance between mouse click and application top left
            widget.dragContext.mouseAnchorX = event.sceneX.toInt()
            widget.dragContext.mouseAnchorY = event.sceneY.toInt()

            //Get the shape which represents this widget
            val node = pane.children.firstOrNull { it is WidgetShape && it.identifier == widget.identifier }

            //Store starting position of widget
            if (node != null) {
                widget.dragContext.anchorX = widget.getX()//Could be node.translateX but both are bound so doesn't matter
                widget.dragContext.anchorY = widget.getY()
            }
        }

        event.consume()
    }

    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown || event.target !is Shape)
            return@EventHandler

        //Return if node selected isn't on canvas
        val pane = getCanvas(event.source) as? PannableCanvas ?: return@EventHandler

        //For all nodes selected (including this one)
        widgets.forSelected { widget ->
            //Set the new position

            //(scene - mouseAnchor) = Difference between click start and current mouse position

            //startPosition + (mouse change offset) / scale
            widget.setX((widget.dragContext.anchorX + (event.sceneX - widget.dragContext.mouseAnchorX) / pane.scale).toInt())//Could also change via node
            widget.setY((widget.dragContext.anchorY + (event.sceneY - widget.dragContext.mouseAnchorY) / pane.scale).toInt())
        }

        event.consume()
    }

    val onMouseEnteredEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        val target = event.target as? WidgetShape ?: return@EventHandler

        val widget = widgets.getWidget(target) ?: return@EventHandler

        widget.setHovered(true)

        event.consume()
    }

    val onMouseExitedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        val target = event.target as? WidgetShape ?: return@EventHandler

        val widget = widgets.getWidget(target) ?: return@EventHandler

        widget.setHovered(false)

        event.consume()
    }

    companion object {
        fun getCanvas(source: Any): Any? {
            if (source is WidgetShape) {
                if (source.parent is PannableCanvas)
                    return source.parent as PannableCanvas
            } else if (source is PannableCanvas)
                return source
            return null
        }
    }
}