package com.greg.canvas.state.selection

import com.greg.canvas.WidgetCanvas
import com.greg.canvas.state.PaneController
import com.greg.canvas.state.selection.marquee.MarqueeController
import com.greg.canvas.state.selection.movement.MovementController
import com.greg.canvas.widget.Widget
import javafx.event.EventTarget
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape


class SelectionController(override var canvas: WidgetCanvas) : PaneController {

    private var selection = WidgetSelection(canvas.selectionGroup, canvas.canvasPane)
    private var movement = MovementController(canvas.selectionGroup, canvas.canvasPane, selection)
    private var marquee = MarqueeController(canvas, canvas.canvasPane, selection)

    override fun handleMousePress(event: MouseEvent) {
        //Get the parent widget (can be null)
        val widget = getWidget(event.target)

        marquee.init(event, widget)

        selection.init(event, widget)

        movement.init(event)
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            //Transform marquee box or selected shapes to match mouse position
            if((movement.cloned && event.isShiftDown && event.target == null) || !marquee.handleSelecting(event))
                movement.drag(event, getWidget(event.target))
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        movement.resetClone()
        marquee.select(event)
    }

    override fun handleDoubleClick(event: MouseEvent) {
        val widget = getWidget(event.target)
        if(widget != null) {
            selection.clear()
            widget.toFront()
            selection.add(widget)
            canvas.controller.edit(widget)
        }
    }

    override fun handleMouseClick(event: MouseEvent) {
        canvas.canvasPane.requestFocus()
    }

    override fun handleKeyPress(event: KeyEvent) {
        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            movement.move(event)

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.A -> {
                    selection.selectAll()
                }
                KeyCode.X -> {
                    selection.copy()
                    selection.delete()
                }
                KeyCode.C -> {
                    selection.copy()
                }
                KeyCode.V -> {
                    selection.paste()
                }
                else -> {
                }
            }
        }

        //Stops the key event here
        event.consume()
    }

    override fun handleKeyRelease(event: KeyEvent) {

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            movement.reset(event.code)
        else if(event.code == KeyCode.DELETE)
            selection.delete()
        else if(!event.isShiftDown)
            movement.resetClone()

        //Stops the key event here
        event.consume()
    }

    /**
     * Convenience functions
     */

    private fun getWidget(target: EventTarget?): Widget? {
        if (target is Shape) {
            val parent = target.parent
            if (parent is Widget)
                return parent
        }

        return null
    }
}