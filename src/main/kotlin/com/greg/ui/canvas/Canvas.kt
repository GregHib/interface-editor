package com.greg.ui.canvas

import com.greg.ui.canvas.selection.Selection
import com.greg.ui.canvas.state.StateManager
import javafx.application.Platform
import javafx.geometry.Bounds
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import tornadofx.Controller
import java.util.*
import kotlin.concurrent.timerTask

class Canvas(private val controller: com.greg.controller.OldController) : Controller() {
    var pane: Pane = controller.canvasView.pane
    private val panels = controller.panels
    private val hierarchy = controller.hierarchy
    var selection = Selection(controller.widgets)
    var manager = StateManager(this, controller.widgets)

    fun handleMouseEvent(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> manager.handleMousePress(event)
            MouseEvent.MOUSE_DRAGGED -> manager.handleMouseDrag(event)
            MouseEvent.MOUSE_RELEASED -> manager.handleMouseRelease(event)
            MouseEvent.MOUSE_CLICKED -> if (event.clickCount == 2) manager.handleDoubleClick(event) else manager.handleMouseClick(event)
        }
    }

    fun handleKeyPress(event: KeyEvent) {
        manager.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        manager.handleKeyRelease(event)
    }

    fun layoutBounds(): Bounds {
        return pane.localToScene(pane.layoutBounds)
    }

    private val timer = Timer()
    private var latest: Long = 0
    private val refresh = Runnable {
        Platform.runLater {
            controller.panels.reload()
            controller.hierarchy.reload()
        }
    }

    fun refreshSelection() {
        //Record the time the refresh was requested
        val start = System.nanoTime()
        //Set as the latest time requested
        latest = start

        //Schedule the run
        timer.schedule(timerTask {
            //Cancel if another refresh was requested less than 10 milliseconds after this one
            if(start < latest) {
                cancel()
                return@timerTask
            }
            //Run refresh
            refresh.run()
        }, 10)//milliseconds
    }
}