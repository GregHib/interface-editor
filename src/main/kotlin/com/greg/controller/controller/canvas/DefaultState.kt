package com.greg.controller.controller.canvas

import com.greg.controller.controller.SelectionController
import com.greg.controller.view.CanvasView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

class DefaultState(canvas: CanvasView) : CanvasState(canvas) {
    val widgets = canvas.widgets
    val pane = canvas.pane
    val selection = SelectionController(widgets)
    val movement = MovementController(widgets, pane)
    var marquee = MarqueeController(widgets, canvas.pane)

    override fun handleMousePress(event: MouseEvent) {
        selection.start(event)

        val cloned = event.isShiftDown && widgets.hasSelection()
        if(cloned)
            movement.clone()

        //Start movement (and actions)
        movement.start(event, pane)

        //If shift cloned start action with cloned widget
        if(cloned)
            widgets.start(widgets.getWidget(movement.getClone(event)))
        else
            widgets.start(widgets.getWidget(event.target))

        marquee.init(event)
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            //Transform marquee box or selected shapes to match mouse position
            if ((movement.cloned && event.isShiftDown && event.target == null)|| !marquee.handle(event))
                movement.drag(event)
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        movement.resetClone()
        marquee.select(event)

        widgets.finish()
    }

    override fun handleDoubleClick(event: MouseEvent) {
        val widget = widgets.getWidget(event.target)
        if (widget != null && !widget.isLocked()) {
            val shape = widgets.getShape(event.target)
            widgets.clearSelection()
            shape?.toFront()
            widget.setSelected(true)
            canvas.edit(widget)
        }
    }

    override fun handleMouseClick(event: MouseEvent) {
        canvas.pane.requestFocus()
    }

    override fun handleKeyPress(event: KeyEvent) {
        if (event.code != KeyCode.SHIFT)
            widgets.start()

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            movement.move(event)

        //Stops the key event here
        event.consume()
    }

    override fun handleKeyRelease(event: KeyEvent) {

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            movement.reset(event.code)
        else if (event.code == KeyCode.DELETE)
            widgets.deleteSelection(pane)
        else if (!event.isShiftDown)
            movement.resetClone()

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.A -> {
                    widgets.selectAll()
                }
                KeyCode.X -> {
                    widgets.cut(pane)
                }
                KeyCode.C -> {
                    widgets.copy()
                }
                KeyCode.V -> {
                    widgets.paste(pane)
                }
                KeyCode.Z -> {
                    if (event.isShiftDown)
                        widgets.redo(pane)
                    else
                        widgets.undo(pane)
                }
                else -> {
                }
            }
        }

        if (event.code != KeyCode.SHIFT)
            widgets.finish()

        //Stops the key event here
        event.consume()
    }

}