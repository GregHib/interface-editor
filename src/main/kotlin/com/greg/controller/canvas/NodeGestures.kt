package src.com.greg.controller.canvas

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape
import src.com.greg.controller.widgets.WidgetsController
import src.com.greg.view.WidgetShape

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
class NodeGestures(val widgets: WidgetsController, private var canvas: PannableCanvas) {

    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown || event.target !is Shape)
            return@EventHandler

        val pane = getCanvas(event.source) as? PannableCanvas ?: return@EventHandler

        widgets.forSelected { widget ->
            widget.dragContext.mouseAnchorX = event.sceneX.toInt()
            widget.dragContext.mouseAnchorY = event.sceneY.toInt()

            var node = pane.children.firstOrNull { it is WidgetShape && it.identifier == widget.identifier }

            if(node != null) {
                widget.dragContext.translateAnchorX = node.translateX.toInt()
                widget.dragContext.translateAnchorY = node.translateY.toInt()
            }
        }

        event.consume()
    }

    // left mouse button => dragging
    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown || event.target !is Shape)
            return@EventHandler

        val pane = getCanvas(event.source) as? PannableCanvas ?: return@EventHandler

        widgets.forSelected { widget ->
            val scale = canvas.scale

            var node = pane.children.firstOrNull { it is WidgetShape && it.identifier == widget.identifier }

            node?.translateX = (widget.dragContext.translateAnchorX + (event.sceneX - widget.dragContext.mouseAnchorX) / scale).toInt().toDouble()
            node?.translateY = (widget.dragContext.translateAnchorY + (event.sceneY - widget.dragContext.mouseAnchorY) / scale).toInt().toDouble()
        }

        event.consume()
    }

    fun getCanvas(source: Any): Any? {
        if(source is WidgetShape) {
            if(source.parent is PannableCanvas)
                return source.parent as PannableCanvas
        } else if(source is PannableCanvas)
            return source
        return null
    }
}