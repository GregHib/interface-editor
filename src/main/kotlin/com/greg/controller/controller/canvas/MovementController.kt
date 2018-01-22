package com.greg.controller.controller.canvas

import com.greg.Utils
import com.greg.controller.controller.WidgetsController
import com.greg.controller.model.Widget
import com.greg.controller.view.WidgetShape
import com.greg.ui.canvas.movement.StartPoint
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane

class MovementController(val widgets: WidgetsController, val pane: Pane) {
    var cloned = false

    fun resetClone() {
        cloned = false
    }

    fun clone() {
        if (!cloned) {
            widgets.clone()
            cloned = true
        }
    }

    fun start(event: MouseEvent, pane: Pane) {
        pane.children
                .filterIsInstance<WidgetShape>()
                .forEach { start(it, event, pane) }
    }

    fun getClone(event: MouseEvent): WidgetShape? {
        return pane.children
                .filterIsInstance<WidgetShape>()
                .firstOrNull { it.boundsInParent.intersects(event.x, event.y, 1.0, 1.0) }
    }

    fun start(shape: WidgetShape, event: MouseEvent, pane: Pane) {
        val widget = widgets.getWidget(shape)
        if (widget != null && widget.isSelected()) {
            val offsetX = pane.localToScene(shape.boundsInParent).minX - event.sceneX
            val offsetY = pane.localToScene(shape.boundsInParent).minY - event.sceneY
            widget.start = StartPoint(offsetX.toInt(), offsetY.toInt())
        }
    }

    fun drag(event: MouseEvent) {
        widgets.forSelected { widget ->
            val x = event.x + widget.start.offsetX
            val y = event.y + widget.start.offsetY
            moveWidget(widget, x, y)
        }
    }

    private var horizontal = 0.0
    private var vertical = 0.0

    fun move(event: KeyEvent) {
        when {
            event.code == KeyCode.RIGHT -> horizontal = 1.0
            event.code == KeyCode.LEFT -> horizontal = -1.0
            event.code == KeyCode.UP -> vertical = -1.0
            event.code == KeyCode.DOWN -> vertical = 1.0
        }

        move(if (event.isShiftDown) horizontal * 10.0 else horizontal, if (event.isShiftDown) vertical * 10.0 else vertical)
    }

    fun reset(code: KeyCode) {
        if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
            horizontal = 0.0

        if (code == KeyCode.UP || code == KeyCode.DOWN)
            vertical = 0.0
    }

    fun move(x: Double, y: Double) {
        widgets.forSelected { widget ->
            move(widget, x, y)
        }
    }

    private fun move(widget: Widget, deltaX: Double, deltaY: Double) {
        //Bounds of the container
        val bounds = pane.localToScene(pane.layoutBounds)

        //Size of shape
        val width = widget.getWidth()
        val height = widget.getHeight()

        val targetX = widget.getX() + deltaX
        val targetY = widget.getY() + deltaY

        //Constrain position to within the container
        val x = Utils.constrain(targetX, bounds.width - width)
        val y = Utils.constrain(targetY, bounds.height - height)

        //Move
        widget.setX(x.toInt())
        widget.setY(y.toInt())
    }

    fun moveWidget(widget: Widget, targetX: Double, targetY: Double) {
        //Bounds of the container
        val bounds = pane.localToScene(pane.layoutBounds)

        //Size of shape
        val width = widget.getWidth()
        val height = widget.getHeight()

        //Constrain position to within the container
        val x = Utils.constrain(targetX, bounds.width - width)
        val y = Utils.constrain(targetY, bounds.height - height)

        //Move
        widget.setX(x.toInt())
        widget.setY(y.toInt())
    }
}