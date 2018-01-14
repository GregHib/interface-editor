package com.greg.controller.controller

import com.greg.controller.controller.canvas.RefreshManager
import com.greg.controller.controller.hierarchy.HierarchyController
import com.greg.controller.controller.panels.PanelController
import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetsModel
import com.greg.controller.view.WidgetShape
import javafx.event.EventTarget
import javafx.scene.shape.Shape
import tornadofx.Controller

class WidgetsController : Controller() {
    val widgets = WidgetsModel()
    private val action = ActionController(this)
    private val panels: PanelController by inject()
    private val hierarchy: HierarchyController by inject()

    val refresh = RefreshManager(panels, hierarchy)

    fun add(widget: Widget) {
        action.add(widget)
        widgets.add(widget)
    }

    fun remove(widget: Widget) {
        action.remove(widget)
        widgets.remove(widget)
    }

    inline fun forSelected(action: (Widget) -> Unit) {
        widgets.forEach { element ->
            if(element.isSelected())
                action(element)
        }
    }

    fun getAll(): List<Widget> {
        return widgets.widgets
    }

    fun hasSelection(): Boolean {
        return widgets.size() > 0
    }

    fun getWidget(target: EventTarget?): Widget? {
        if (target is Shape) {
            val parent = target.parent
            if (parent is WidgetShape)
                return widgets.get(parent)
        }
        return null
    }


    fun getWidget(target: WidgetShape): Widget? {
        return widgets.get(target)
    }

    fun getShape(target: EventTarget?): WidgetShape? {
        if (target is Shape) {
            val parent = target.parent
            if (parent is WidgetShape)
                return parent
        }
        return null
    }

    fun clearSelection() {
        widgets.forEach { widget ->
            if(widget.isSelected())
                widget.setSelected(false)
        }
    }

    fun selectAll() {
        widgets.forEach { widget ->
            if(!widget.isSelected())
                widget.setSelected(true)
        }
    }

    fun clone() {
        action.clone()
    }

    private var counter = 0

    fun start(widget: Widget? = null) {
        if(counter == 0)
            action.start(widget)

        counter++
    }

    fun finish() {
        val was = counter == 0
        if(counter > 0)
            counter--

        if(counter == 0 && !was)
            action.finish()
    }

    fun cut() {
        action.cut()
    }

    fun deleteSelection() {
    }

    fun redo() {
        action.redo()
    }

    fun undo() {
        action.undo()
    }

    fun paste() {
        action.paste()
    }

    fun copy() {
        action.copy()
    }

    fun requestRefresh() {
        refresh.request()
    }

}