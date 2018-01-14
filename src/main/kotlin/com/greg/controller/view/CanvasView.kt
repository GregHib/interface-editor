package com.greg.controller.view

import com.greg.controller.controller.WidgetsController
import com.greg.controller.controller.canvas.StateManager
import com.greg.controller.controller.input.KeyboardController
import com.greg.controller.controller.input.MouseController
import com.greg.controller.controller.panels.PanelController
import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetBuilder
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
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
    val panels: PanelController by inject()
    private val manager = StateManager(this)


    fun edit(widget: Widget) {
        manager.edit(widget)
    }
    fun toggleState() {
        manager.toggle()
    }

    fun createWidget() {}

    fun createContainer() {}

    fun createRectangle() {
        createAndDisplay(WidgetType.WIDGET)
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

        //Selection
        widget.selectedProperty().addListener { _, oldValue, newValue ->
            if(oldValue != newValue)
                shape.rectangle.stroke = Settings.getColour(if(newValue) SettingsKey.SELECTION_STROKE_COLOUR else SettingsKey.DEFAULT_STROKE_COLOUR)
        }

        shape.layoutXProperty().bind(widget.xProperty())
        shape.layoutYProperty().bind(widget.yProperty())
        shape.rectangle.widthProperty().bind(widget.widthProperty())
        shape.rectangle.heightProperty().bind(widget.heightProperty())

        widgets.add(widget)
        pane.children.add(shape)
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