package com.greg.canvas.state.edit

import com.greg.canvas.DragModel
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.edit.resize.ResizeController
import com.greg.canvas.state.edit.resize.ResizeTab
import com.greg.canvas.state.edit.resize.WidgetChangeInterface
import com.greg.canvas.state.edit.resize.WidgetChangeListener
import com.greg.canvas.state.selection.SelectionController
import com.greg.canvas.widget.Widget
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


class EditController(override var canvas: WidgetCanvas, val widget: Widget) : PaneController, WidgetChangeInterface {

    private val controller = ResizeController(canvas, widget)
    private val listener = WidgetChangeListener(this)

    private var path: Shape? = null

    init {
        refresh()

        //Add all tabs to canvas
        controller.start(widget)

        listener.link(widget)
    }

    private fun close() {
        listener.unlink()
        canvas.canvasPane.children.remove(path)
        controller.close()
        canvas.controller = SelectionController(canvas)
    }

    override fun handleMousePress(event: MouseEvent) {
        when {
            event.target == path -> close()
            event.target is ResizeTab -> {
                controller.press(event)
                widget.drag = DragModel(widget.layoutX - event.x, widget.layoutY - event.y)
            }
            event.target is Rectangle -> {
                setWidgetDrag(widget, event, canvas)
            }
        }
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (controller.click != null && widget.drag != null) {
            val target = controller.click?.target
            if (target is ResizeTab) {
                val bounds = canvas.canvasPane.localToScene(canvas.canvasPane.layoutBounds)
                //Get the directional info for the tab selected
                val resizeDir = controller.getDirection(target)

                //Resize all the N S E W values for the tab
                for(direction in resizeDir.directions)
                    controller.resize(direction, event, bounds)
            }
        } else if (event.target is Rectangle) {//Dragging
            moveInCanvas(widget, event)
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        controller.reset()
        widget.drag = null
    }

    override fun handleDoubleClick(event: MouseEvent) {
    }

    override fun handleMouseClick(event: MouseEvent) {
    }

    override fun handleKeyPress(event: KeyEvent) {
        controller.shift = event.isShiftDown
    }

    override fun handleKeyRelease(event: KeyEvent) {
        controller.shift = event.isShiftDown
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
        canvas.canvasPane.children.remove(path)
        path = Path.subtract(rectangle, mask)
        path?.fill = Color.rgb(0, 0, 155, 0.6)
        canvas.canvasPane.children.add(path)
    }
}