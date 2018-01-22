package com.greg.controller.view

import com.greg.controller.controller.WidgetsController
import com.greg.controller.controller.canvas.StateManager
import com.greg.controller.controller.input.KeyboardController
import com.greg.controller.controller.input.MouseController
import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetBuilder
import com.greg.ui.canvas.widget.type.WidgetType
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import tornadofx.View

class CanvasView : View(), MouseController, KeyboardController {
    override val root: VBox by fxml("/canvas.fxml")
    val pane: Pane by fxid()
    val widgets: WidgetsController by inject()
    private val manager = StateManager(this)

    init {
        widgets.pane = pane
    }

    fun edit(widget: Widget) {
        manager.edit(widget)
    }
    fun toggleState() {
        manager.toggle()
    }

    fun createWidget() {
        createAndDisplay(WidgetType.WIDGET)
    }

    fun createContainer() {}

    fun createRectangle() {
        createAndDisplay(WidgetType.RECTANGLE)
    }

    fun createText() {
        createAndDisplay(WidgetType.TEXT)
    }

    fun createSprite() {}

    fun createModel() {}

    fun createTooltip() {}

    private fun createAndDisplay(type: WidgetType) {
        val widget = WidgetBuilder(type).build()
        val shape = WidgetShapeBuilder(widget).build()

        widgets.display(widget, shape)
    }

    override fun handleKeyPress(event: KeyEvent) {
        manager.state.handleKeyPress(event)
    }

    override fun handleKeyRelease(event: KeyEvent) {
        manager.state.handleKeyRelease(event)
    }

    override fun handleMousePress(event: MouseEvent) {
        manager.state.handleMousePress(event)
    }

    override fun handleMouseDrag(event: MouseEvent) {
        manager.state.handleMouseDrag(event)
    }

    override fun handleMouseRelease(event: MouseEvent) {
        manager.state.handleMouseRelease(event)
    }

    override fun handleDoubleClick(event: MouseEvent) {
        manager.state.handleDoubleClick(event)
    }

    override fun handleMouseClick(event: MouseEvent) {
        manager.state.handleMouseClick(event)
    }
}