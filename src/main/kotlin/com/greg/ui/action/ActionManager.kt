package com.greg.ui.action

import com.greg.controller.ControllerView
import com.greg.ui.action.change.Change
import com.greg.ui.action.change.ChangeType
import com.greg.ui.action.containers.ActionList
import com.greg.ui.action.containers.Actions
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.builder.WidgetMementoBuilderAdapter
import com.greg.ui.canvas.widget.memento.mementoes.Memento
import com.greg.ui.canvas.widget.type.types.WidgetGroup

class ActionManager(private val widgets: Widgets, private val controller: ControllerView) {

    private val actions = Actions()
    private val redo = ActionList()
    private var cached: WidgetGroup? = null
    var ignore = false

    fun start(widget: WidgetGroup? = null) {
        actions.start()
        //Must remove ignore before first record
        ignore = false
        if(widget != null)
            record(ChangeType.CHANGE, widget)
    }

    fun finish() {
        if(actions.finish())
            redo.clear()
    }

    fun record(type: ChangeType, widget: WidgetGroup) {
        record(type, widget.identifier, widget.getMemento())
    }

    fun record(type: ChangeType, identifier: Int, memento: Memento) {
        if (!ignore) {
            val change = Change(type, identifier, memento)
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
            cached = null
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
            cached = null
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
            widgets.getAll().add(widget)
            widget.restore(change.memento)
        } else {
            if(cached == null || cached?.identifier != change.id) {
                for (node in widgets.getAll()) {
                    if (node is WidgetGroup) {
                        if (node.identifier == change.id) {
                            cached = node
                            break
                        }
                    }
                }
            }

            if(cached != null) {
                if (remove) {
                    controller.canvas.selection.remove(cached!!)
                    return widgets.getAll().remove(cached)
                } else
                    cached?.restore(change.memento)
            }
        }
        return false
    }

    fun addSingle(add: ChangeType, widget: WidgetGroup) {
        start(widget)
        record(add, widget)
        finish()
    }
}