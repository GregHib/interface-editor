package com.greg.controller

import com.greg.controller.actions.ActionList
import com.greg.controller.actions.Actions
import com.greg.controller.actions.Change
import com.greg.controller.widgets.WidgetMementoBuilderAdapter
import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.Widget
import com.greg.model.widgets.memento.Memento

class ActionController(val widgets: WidgetsController) {
    private val actions = Actions()
    private val redo = ActionList()
    private var cached: Widget? = null
    private var ignore = false
    private val interaction = InteractionController(widgets)

    fun start(widget: Widget?) {
        actions.start()
        //Must remove ignore before first record
        ignore = false
        if (widget != null)
            record(ChangeType.CHANGE, widget)
    }

    fun finish() {
        if (actions.finish())
            redo.clear()
    }

    fun record(type: ChangeType, widget: Widget) {
        if (!ignore)
            record(type, widget.identifier, widget.getMemento())
    }

    fun record(type: ChangeType, identifier: Int, value: Any) {
        val change = Change(type, identifier, value)
        actions.record(change)
    }

    fun add(widget: Widget) {
        record(ChangeType.ADD, widget)
    }

    fun remove(widget: Widget) {
        record(ChangeType.REMOVE, widget)
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
            widgets.requestRefresh()
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
            widgets.requestRefresh()
        }
    }

    private fun applyChange(change: Change, undo: Boolean): Boolean {
        when (change.type) {
            ChangeType.ADD, ChangeType.REMOVE, ChangeType.CHANGE -> {
                return if (undo)
                    applyChange(change, change.type == ChangeType.REMOVE, change.type == ChangeType.ADD)
                else
                    applyChange(change, change.type == ChangeType.ADD, change.type == ChangeType.REMOVE)
            }
            ChangeType.ORDER -> {
                if (change.value is List<*>) {
                    val list = change.value as List<Int>
//                    if (undo)
//                        pane.children.move(pane.children[list[1]], list[0])
//                    else
//                        pane.children.move(pane.children[list[0]], list[1])
                }
                return false
            }
        }
    }

    private fun applyChange(change: Change, add: Boolean, remove: Boolean): Boolean {
        val memento = change.value as Memento
        if (add) {
            val widget = WidgetMementoBuilderAdapter(memento).build(change.id)
            widgets.add(widget)
        } else {
            if (cached == null || cached?.identifier != change.id) {
                for (node in widgets.getAll()) {
                    if (node.identifier == change.id) {
                        cached = node
                        break
                    }
                }
            }

            if (cached != null) {
                if (remove) {
                    cached!!.setSelected(false)
                    widgets.remove(cached!!)
                    return true
                } else
                    cached?.restore(memento)
            }
        }
        return false
    }

    fun addSingle(add: ChangeType, widget: Widget) {
        if (!ignore) {
            start(widget)
            record(add, widget)
            finish()
        }
    }

    fun copy() {
        interaction.copy()
    }

    fun paste() {
        interaction.paste()
    }

    fun clone() {
        interaction.clone()
    }
}