package com.greg.ui.canvas.state.states.edit

import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.movement.MovementProxy
import com.greg.ui.canvas.state.states.CanvasState
import com.greg.ui.canvas.state.states.edit.resize.box.ResizeBox
import com.greg.ui.canvas.state.states.edit.resize.box.points.ResizePoint
import com.greg.ui.canvas.state.states.edit.resize.observer.AttributeObserver
import com.greg.ui.canvas.state.states.edit.resize.observer.WidgetObserver
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


class EditState(override var canvas: Canvas, val widget: WidgetGroup) : CanvasState, WidgetObserver {

    private val resize = ResizeBox(widget, canvas.pane)
    private val observer = AttributeObserver(this)
    private var movement = MovementProxy(canvas.pane, canvas.selection)

    private var path: Shape? = null

    init {
        refresh()

        //Add all tabs to canvas
        resize.start(widget)

        observer.link(widget)
    }

    private fun close() {
        observer.unlink()
        canvas.pane.children.remove(path)
        resize.close()
        canvas.manager.select()
    }

    override fun handleMousePress(event: MouseEvent) {
        when {
            event.target == path -> close()
            event.target is ResizePoint -> {
                resize.press(event)
                movement.start(widget, widget.layoutX - event.x, widget.layoutY - event.y)
            }
            event.target is Rectangle -> {
                movement.start(widget, event, canvas.pane)
            }
        }
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (resize.click != null && widget.start != null) {
            val target = resize.click?.target
            if (target is ResizePoint) {
                val bounds = canvas.layoutBounds()
                //Get the directional info for the tab selected
                val resizeDir = resize.getDirection(target)

                //Resize all the N S E W values for the tab
                for(direction in resizeDir.directions)
                    resize.resize(direction, event, bounds)
            }
        } else if (event.target is Rectangle) {
            movement.drag(event)
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        resize.reset()
        widget.start = null
    }

    override fun handleDoubleClick(event: MouseEvent) {
    }

    override fun handleMouseClick(event: MouseEvent) {
    }

    override fun handleKeyPress(event: KeyEvent) {
        resize.shift = event.isShiftDown
    }

    override fun handleKeyRelease(event: KeyEvent) {
        resize.shift = event.isShiftDown
    }

    override fun onChange() {
        refresh()
    }

    private fun refresh() {
        //TODO Better way rather than remove/recreate every time?
        val node = widget.getNode()
        val rect = widget.getRectangle().getNode() as Rectangle

        val mask = Rectangle(node.layoutX, node.layoutY, rect.width, rect.height)
        val rectangle = Rectangle(765.0, 503.0)
        canvas.pane.children.remove(path)
        path = Path.subtract(rectangle, mask)
        path?.fill = Color.rgb(0, 0, 155, 0.6)
        canvas.pane.children.add(path)
    }
}