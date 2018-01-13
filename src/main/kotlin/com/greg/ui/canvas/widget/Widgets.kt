package com.greg.ui.canvas.widget

import com.greg.controller.ControllerView
import com.greg.ui.action.ActionManager
import com.greg.ui.action.change.ChangeType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.collections.ObservableList
import javafx.scene.Node

class Widgets(val controller: ControllerView) {
    private val pane = controller.widgetCanvas
    val manager = ActionManager(this, controller)
    private var counter = 0

    fun start(widget: WidgetGroup? = null) {
        if(counter == 0)
            manager.start(widget)

        counter++
    }

    fun finish() {
        val was = counter == 0
        if(counter > 0)
            counter--

        if(counter == 0 && !was)
            manager.finish()
    }

    fun add(widget: WidgetGroup): Boolean {
        recordSingle(ChangeType.ADD, widget)
        controller.hierarchy.add(widget)
        return getAll().add(widget)
    }

    fun remove(widget: WidgetGroup): Boolean {
        recordSingle(ChangeType.REMOVE, widget)
        controller.hierarchy.remove(widget)
        return getAll().remove(widget)
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
        manager.undo()
    }

    fun redo() {
        manager.redo()
    }

    fun record(type: ChangeType, widget: WidgetGroup) {
        manager.record(type, widget)
    }

    private fun recordSingle(type: ChangeType, widget: WidgetGroup) {
        manager.addSingle(type, widget)
    }
}