package com.greg.selection

import com.greg.widget.Widget
import com.greg.widget.WidgetRectangle
import com.greg.widget.WidgetText
import com.sun.deploy.uitoolkit.DragContext
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineCap

class RubberBandSelection(private var group: Pane, private var selectionModel: SelectionModel) {

    private val dragContext = DragContext()
    private var rect: Rectangle = Rectangle(0.0, 0.0, 0.0, 0.0)
    private var selecting = false

    private fun getX(event: MouseEvent): Double {
        val x = group.localToScene(group.boundsInLocal).minX
        return if (event.sceneX < x) 0.0 else if (event.sceneX > x + group.width) group.width else event.sceneX - group.localToScene(group.boundsInLocal).minX
    }

    private fun getY(event: MouseEvent): Double {
        val y = group.localToScene(group.boundsInLocal).minY
        return if (event.sceneY < y) 0.0 else if (event.sceneY > y + group.height) group.height else event.sceneY - group.localToScene(group.boundsInLocal).minY
    }

    private var onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        /**
         * TODO
         * add if clicked on shape
         * and released on shape without dragging, select just that shape
         *
         * if multiple selected and click and drag, all move (and on release all stay selected)
         *
         * if one clicked one selected,
         *
         * if another clicked another selected
         *
         * if another clicked with control down two are now selected
         *
         * if one clicked which was already selected with control down, remove selection on that one
         */
        if (event.target is Pane) {
            dragContext.mouseAnchorX = getX(event)
            dragContext.mouseAnchorY = getY(event)

            rect.x = dragContext.mouseAnchorX
            rect.y = dragContext.mouseAnchorY
            rect.width = 0.0
            rect.height = 0.0

            group.children.add(rect)

            event.consume()
            selecting = true
        } else if(event.target is Shape) {
            var node = event.target as Shape
            if(node is Widget) {
                if(selectionModel.size() == 1 && !event.isShiftDown && !event.isControlDown)
                    selectionModel.clear()
                selectionModel.add(node)
            }

            node.toFront()

            selectionModel.getSelection().forEach { n ->
                if(n is Widget) {
                    n.drag = DragModel(event.sceneX, event.sceneY, n.translateX, n.translateY)
                }
            }
        }
    }

    private var onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (selecting) {
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
        } else {
            if (selectionModel.contains(event.target as Shape)) {
                selectionModel.getSelection().forEach { n ->
                    if(n is Widget && n.drag != null) {
                        val offsetX = event.sceneX - n.drag.sceneX
                        val offsetY = event.sceneY - n.drag.sceneY
                        val newTranslateX = n.drag.translateX + offsetX
                        val newTranslateY = n.drag.translateY + offsetY

                        n.translateX = newTranslateX
                        n.translateY = newTranslateY
                    }
                }
            }
        }
    }

    private var onMouseReleasedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isShiftDown && !event.isControlDown)
            selectionModel.clear()
        if (selecting) {
            group.children
                    .filter { it is Widget && it.boundsInParent.intersects(rect.boundsInParent) }
                    .forEach {
                        var shape = it as Shape
                        if (event.isShiftDown) {
                            selectionModel.add(shape)
                        } else if (event.isControlDown) {
                            if (selectionModel.contains(shape)) {
                                selectionModel.remove(shape)
                            } else {
                                selectionModel.add(shape)
                            }
                        } else {
                            selectionModel.add(shape)
                        }
                    }

            selectionModel.log()

            rect.x = 0.0
            rect.y = 0.0
            rect.width = 0.0
            rect.height = 0.0

            group.children.remove(rect)

            event.consume()
            selecting = false
        }
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