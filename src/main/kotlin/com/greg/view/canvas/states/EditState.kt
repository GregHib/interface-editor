package com.greg.view.canvas.states

import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.widgets.WidgetsController
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.Widget
import com.greg.view.canvas.states.edit.ResizeBox
import com.greg.view.canvas.states.edit.ResizePoint
import com.greg.view.canvas.CanvasState
import com.greg.view.canvas.CanvasView
import com.greg.view.canvas.widgets.WidgetShape
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent

class EditState(private val view: CanvasView, private var widget: Widget, shape: WidgetShape, val widgets: WidgetsController, private val canvas: PannableCanvas) : CanvasState {

    private val start = widget.getMemento()
    private var resize = ResizeBox(widget, canvas)

    init {
        resize.start(shape)
    }

    private fun close() {
        resize.close()
        view.defaultState()
    }

    override fun handleMousePress(event: MouseEvent) {
        if (event.target != canvas)
            widgets.start(widget)

        when {
            event.target is ResizePoint -> {
                resize.press(event)

                //Set start mouse position
                widget.dragContext.mouseAnchorX = event.sceneX.toInt()
                widget.dragContext.mouseAnchorY = event.sceneY.toInt()

                //Set starting position
                widget.dragContext.anchorX = widget.getX()
                widget.dragContext.anchorY = widget.getY()
            }
        }
    }

    override fun handleMouseDrag(event: MouseEvent) {
        if (resize.click != null) {
            val target = resize.click?.target
            if (target is ResizePoint) {
                //Get the directional info for the tab selected
                val resizeDir = resize.getDirection(target)

                //Resize all the N S E W values for the tab
                for (direction in resizeDir.directions)
                    resize.resize(direction, event)
            }
        }
    }

    override fun handleMouseRelease(event: MouseEvent) {
        //Close edit state if clicked on empty space
        if (!CanvasView.spaceHeld && event.target == canvas)
            close()

        resize.reset()
        widgets.finish()
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

        when (event.code.ordinal) {
            Settings.getInt(Settings.ACCEPT_KEY_CODE) -> close()
            Settings.getInt(Settings.CANCEL_KEY_CODE) -> {
                widget.restore(start)
                close()
            }
        }

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.Z -> {
                    if (event.isShiftDown)
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


    override fun onClose() {
    }
}