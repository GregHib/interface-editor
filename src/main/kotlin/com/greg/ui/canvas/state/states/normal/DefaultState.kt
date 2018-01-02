package com.greg.ui.canvas.state.states.normal

import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.movement.MovementProxy
import com.greg.ui.canvas.state.states.CanvasState
import com.greg.ui.canvas.state.states.normal.selection.marquee.MarqueeFacade
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.event.EventTarget
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Shape


class DefaultState(override var canvas: Canvas, private val widgets: Widgets) : CanvasState {

    private var selection = canvas.selection
    private var movement = MovementProxy(canvas.pane, selection)
    private var marquee = MarqueeFacade(canvas.pane)

    override fun handleMousePress(event: MouseEvent) {

        //Get the parent widget (can be null)
        val widget = getWidget(event.target)

        widgets.start(widget)

        marquee.init(event, widget)

        selection.init(event, widget)

        movement.init(event)

    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (event.isPrimaryButtonDown) {
            //Transform marquee box or selected shapes to match mouse position
            if((movement.cloned && event.isShiftDown && event.target == null) || !marquee.handle(event, selection))
                movement.drag(event)
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        movement.resetClone()
        marquee.select(event, selection, canvas)

        widgets.finish()
    }

    override fun handleDoubleClick(event: MouseEvent) {
        val widget = getWidget(event.target)
        if(widget != null) {
            selection.clear()
            widget.toFront()
            selection.add(widget)
            canvas.manager.edit(widget)
        }
    }

    override fun handleMouseClick(event: MouseEvent) {
        canvas.pane.requestFocus()
    }

    override fun handleKeyPress(event: KeyEvent) {
        widgets.start()

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            movement.move(event)

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.A -> {
                    selection.selectAll()
                }
                KeyCode.X -> {
                    selection.copy()
                    selection.deleteAll()
                }
                KeyCode.C -> {
                    selection.copy()
                }
                KeyCode.V -> {
                    selection.paste()
                }
                KeyCode.Z -> {
                    if(event.isShiftDown)
                        widgets.redo()
                    else
                        widgets.undo()
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
            selection.deleteAll()
        else if(!event.isShiftDown)
            movement.resetClone()

        widgets.finish()

        //Stops the key event here
        event.consume()
    }
    /**
     * Convenience functions
     */

    private fun getWidget(target: EventTarget?): WidgetGroup? {
        if (target is Shape) {
            val parent = target.parent
            if (parent is WidgetGroup)
                return parent
        }

        return null
    }
}