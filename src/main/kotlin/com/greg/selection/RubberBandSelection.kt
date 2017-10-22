package com.greg.selection

import com.greg.widget.Widget
import com.greg.widget.WidgetRectangle
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap

class RubberBandSelection(private var group: Pane, private var selectionModel: SelectionModel) {
    private val dragContext = DragContext()
    private var rect: Rectangle = Rectangle(0.0, 0.0, 0.0, 0.0)

    private fun getX(event: MouseEvent): Double {
        val x = group.localToScene(group.boundsInLocal).minX
        return if(event.sceneX < x) 0.0 else if(event.sceneX > x + group.width) group.width else event.sceneX - group.localToScene(group.boundsInLocal).minX
    }

    private fun getY(event: MouseEvent): Double {
        val y = group.localToScene(group.boundsInLocal).minY
        return if(event.sceneY < y) 0.0 else if(event.sceneY > y + group.height) group.height else event.sceneY - group.localToScene(group.boundsInLocal).minY
    }

    private var onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        dragContext.mouseAnchorX = getX(event)
        dragContext.mouseAnchorY = getY(event)

        rect.x = dragContext.mouseAnchorX
        rect.y = dragContext.mouseAnchorY
        rect.width = 0.0
        rect.height = 0.0

        group.children.add(rect)

        event.consume()
    }

    private var onMouseReleasedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isShiftDown && !event.isControlDown) {
            selectionModel.clear()
        }

        group.children
                .filter { it is Widget && it.boundsInParent.intersects(rect.boundsInParent) }
                .forEach {
                    if (event.isShiftDown) {
                        selectionModel.add(it)
                    } else if (event.isControlDown) {

                        if (selectionModel.contains(it)) {
                            selectionModel.remove(it)
                        } else {
                            selectionModel.add(it)
                        }
                    } else {
                        selectionModel.add(it)
                    }
                }

        selectionModel.log()

        rect.x = 0.0
        rect.y = 0.0
        rect.width = 0.0
        rect.height = 0.0

        group.children.remove(rect)

        event.consume()
    }

    private var onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        val offsetX = getX(event) - dragContext.mouseAnchorX
        val offsetY = getY(event) - dragContext.mouseAnchorY

        if (offsetX > 0)
            rect.width = offsetX
        else {
            rect.x = getX(event)
            rect.width = dragContext.mouseAnchorX - rect.x
        }

        if (offsetY > 0) {
            rect.height = offsetY
        } else {
            rect.y = getY(event)
            rect.height = dragContext.mouseAnchorY - rect.y
        }

        event.consume()
    }

    init {
        rect.stroke = Color.BLUE
        rect.strokeWidth = 1.0
        rect.strokeLineCap = StrokeLineCap.ROUND
        rect.fill = Color.LIGHTBLUE.deriveColor(0.0, 1.2, 1.0, 0.6)

        group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler)
        group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler)
        group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler)

    }

    private inner class DragContext {
        var mouseAnchorX: Double = 0.toDouble()
        var mouseAnchorY: Double = 0.toDouble()
    }
}