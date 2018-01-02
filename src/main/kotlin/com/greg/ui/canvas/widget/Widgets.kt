package com.greg.ui.canvas.widget

import com.greg.controller.Controller
import com.greg.ui.action.ActionManager
import com.greg.ui.action.change.ChangeType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.collections.ObservableList
import javafx.scene.Node

class Widgets(controller: Controller) {
    private val pane = controller.widgetCanvas
    private val manager = ActionManager(pane, controller)
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
        return getAll().add(widget)
    }

    fun remove(widget: WidgetGroup): Boolean {
        recordSingle(ChangeType.REMOVE, widget)
        return getAll().remove(widget)
    }

    fun getAll(): ObservableList<Node> {
        return pane.children
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