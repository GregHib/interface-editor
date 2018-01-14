package com.greg.ui.canvas.widget

import com.greg.controller.ControllerView
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.action.change.ChangeType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.collections.ObservableList
import javafx.scene.Node

class Widgets(private val controller: ControllerView) {
    private val pane = controller.widgetCanvas
    private var counter = 0

    fun start(widget: WidgetGroup? = null) {
        if(counter == 0)
            controller.manager.start(widget)

        counter++
    }

    fun finish() {
        val was = counter == 0
        if(counter > 0)
            counter--

        if(counter == 0 && !was)
            controller.manager.finish()
    }

    fun add(widget: WidgetGroup) {
        recordSingle(ChangeType.ADD, widget)

        widget.lockedProperty().addListener { _, _, newValue ->
            if(newValue)
                widget.setSelected(false)
        }

        widget.selectedProperty().addListener { _, oldValue, newValue ->
            if(oldValue != newValue) {
                widget.setSelection(Settings.getColour(if(newValue) SettingsKey.SELECTION_STROKE_COLOUR else SettingsKey.DEFAULT_STROKE_COLOUR))
                controller.canvas.refreshSelection()
            }
        }

        controller.hierarchy.add(widget)
        getAll().add(widget)
    }

    fun remove(widget: WidgetGroup) {
        recordSingle(ChangeType.REMOVE, widget)
        controller.hierarchy.remove(widget)
        getAll().remove(widget)
    }

    fun getAll(): ObservableList<Node> {
        return pane.children
    }

    inline fun forWidgets(action: (WidgetGroup) -> Unit) {
        getAll()
                .filterIsInstance<WidgetGroup>()
                .forEach { action(it) }
    }

    inline fun forWidgetsReversed(action: (WidgetGroup) -> Unit) {
        getAll()
                .filterIsInstance<WidgetGroup>()
                .reversed()
                .forEach { action(it) }
    }

    fun undo() {
        controller.manager.undo()
    }

    fun redo() {
        controller.manager.redo()
    }

    fun record(type: ChangeType, widget: WidgetGroup) {
        controller.manager.record(type, widget)
    }

    private fun recordSingle(type: ChangeType, widget: WidgetGroup) {
        controller.manager.addSingle(type, widget)
    }
}