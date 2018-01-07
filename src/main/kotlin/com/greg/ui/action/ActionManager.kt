package com.greg.ui.action

import com.greg.controller.ControllerView
import com.greg.ui.action.change.Change
import com.greg.ui.action.change.ChangeType
import com.greg.ui.action.containers.ActionList
import com.greg.ui.action.containers.Actions
import com.greg.ui.canvas.widget.builder.WidgetMementoBuilderAdapter
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import javafx.scene.layout.Pane

class ActionManager(private val pane: Pane, private val controller: ControllerView) {

    private val actions = Actions()
    private val redo = ActionList()
    var ignore = false

    fun record(type: ChangeType, widget: WidgetGroup) {
        if (!ignore) {
            val change = Change(type, widget.identifier, widget.getMemento())
            actions.record(change)
        }
    }

    @Suppress("LoopToCallChain")
    fun undo() {
        if (actions.isNotEmpty()) {
            ignore = true
            val last = actions.last()
            for (change in last.getChanges().reversed())
                if (applyChange(change, true))
                    break
            actions.remove(last)
            redo.add(last)
        }
    }

    @Suppress("LoopToCallChain")
    fun redo() {
        if (redo.isNotEmpty()) {
            ignore = true
            val last = redo.last()
            for (change in last.getChanges())
                if (applyChange(change, false))
                    break
            redo.remove(last)
            actions.add(last)
        }
    }

    private fun applyChange(change: Change, undo: Boolean) : Boolean {
        return if(undo)
            applyChange(change, change.type == ChangeType.REMOVE, change.type == ChangeType.ADD)
        else
            applyChange(change, change.type == ChangeType.ADD, change.type == ChangeType.REMOVE)
    }

    private fun applyChange(change: Change, add: Boolean, remove: Boolean): Boolean {
        if (add) {
            val widget = WidgetMementoBuilderAdapter(change.memento).build(change.id)
            pane.children.add(widget)
            widget.restore(change.memento)
        } else {
            for (node in pane.children) {
                if (node is WidgetGroup) {
                    if (node.identifier == change.id) {
                        if (remove) {
                            controller.canvas.selection.remove(node)
                            return pane.children.remove(node)
                        } else
                            node.restore(change.memento)
                        break
                    }
                }
            }
        }
        return false
    }

    fun start(widget: WidgetGroup? = null) {
        if(widget != null) {
            actions.start()
            record(ChangeType.CHANGE, widget)
        }

        ignore = false
    }

    fun finish() {
        if(actions.finish())
            redo.clear()
    }

    fun addSingle(add: ChangeType, widget: WidgetGroup) {
        start(widget)
        record(add, widget)
        finish()
    }
}