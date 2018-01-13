package com.greg.ui.canvas.state.states.edit

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.movement.MovementProxy
import com.greg.ui.canvas.state.states.CanvasState
import com.greg.ui.canvas.state.states.edit.resize.box.ResizeBox
import com.greg.ui.canvas.state.states.edit.resize.box.points.ResizePoint
import com.greg.ui.canvas.state.states.edit.resize.observer.AttributeObserver
import com.greg.ui.canvas.state.states.edit.resize.observer.WidgetObserver
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


class EditState(override var canvas: Canvas, private val widgets: Widgets, val widget: WidgetGroup) : CanvasState, WidgetObserver {

    private val start = widget.getMemento()
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
        if(event.target != path) {
            widgets.start(widget)
        }
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
        widgets.finish()
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

        when(event.code.ordinal) {
            Settings.getInt(SettingsKey.ACCEPT_KEY_CODE) -> { close() }
            Settings.getInt(SettingsKey.CANCEL_KEY_CODE) -> {
                widget.restore(start)
                close()
            }
        }

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.Z -> {
                    if(event.isShiftDown)
                        widgets.redo()
                    else
                        widgets.undo()
                    close()
                }
                else -> {
                }
            }
        }

        //Stops the key event here
        event.consume()
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