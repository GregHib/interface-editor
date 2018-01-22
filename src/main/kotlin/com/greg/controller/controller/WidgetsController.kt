package com.greg.controller.controller

import com.greg.controller.controller.canvas.RefreshManager
import com.greg.controller.controller.hierarchy.HierarchyController
import com.greg.controller.controller.panels.PanelController
import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetRectangle
import com.greg.controller.model.WidgetText
import com.greg.controller.model.WidgetsModel
import com.greg.controller.view.RectangleShape
import com.greg.controller.view.TextShape
import com.greg.controller.view.WidgetShape
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.action.change.ChangeType
import javafx.event.EventTarget
import javafx.scene.layout.Pane
import javafx.scene.shape.Shape
import tornadofx.Controller
import tornadofx.DefaultScope

class WidgetsController : Controller() {
    val widgets = WidgetsModel()
    lateinit var pane: Pane
    private val hierarchy: HierarchyController by inject(DefaultScope)
    private val action = ActionController(this)

    val panels = PanelController(this)

    val refresh = RefreshManager(panels, hierarchy)

    private fun add(widget: Widget) {
        recordSingle(ChangeType.ADD, widget)
        widgets.add(widget)
    }

    private fun remove(widget: Widget) {
        recordSingle(ChangeType.REMOVE, widget)
        widgets.remove(widget)
    }

    inline fun forSelected(action: (Widget) -> Unit) {
        getSelection().forEach { element ->
            action(element)
        }
    }

    fun getAll(): List<Widget> {
        return widgets.get()
    }

    fun getSelection(): List<Widget> {
        return widgets.get().filter { it.isSelected() }
    }

    fun hasSelection(): Boolean {
        return getSelection().isNotEmpty()
    }

    fun size(): Int {
        return widgets.size()
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
            if (widget.isSelected())
                widget.setSelected(false)
        }
    }

    fun selectAll() {
        widgets.forEach { widget ->
            if (!widget.isSelected())
                widget.setSelected(true)
        }
    }

    fun clone() {
        action.clone()
    }

    private var counter = 0

    fun start(widget: Widget? = null) {
        if (counter == 0)
            action.start(widget)

        counter++
    }

    fun finish() {
        val was = counter == 0
        if (counter > 0)
            counter--

        if (counter == 0 && !was)
            action.finish()
    }

    fun cut() {
        action.copy()
        deleteSelection()
    }

    fun deleteSelection() {
        val selection = mutableListOf<Widget>()
        widgets.forEach { widget ->
            if (widget.isSelected())
                selection.add(widget)
        }

        selection.forEach { widget ->
            destroy(widget)
        }
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

    fun display(widget: Widget, shape: WidgetShape) {
        //Selection
        widget.selectedProperty().addListener { _, oldValue, newValue ->
            if (oldValue != newValue) {
                shape.outline.toFront()
                shape.outline.stroke = Settings.getColour(if (newValue) SettingsKey.SELECTION_STROKE_COLOUR else SettingsKey.DEFAULT_STROKE_COLOUR)
                requestRefresh()
            }
        }

        shape.layoutXProperty().bind(widget.xProperty())
        shape.layoutYProperty().bind(widget.yProperty())
        shape.outline.widthProperty().bind(widget.widthProperty())
        shape.outline.heightProperty().bind(widget.heightProperty())

        if(widget is WidgetRectangle && shape is RectangleShape) {
            shape.rectangle.fillProperty().bind(widget.fillProperty())
            shape.rectangle.strokeProperty().bind(widget.strokeProperty())
        } else if(widget is WidgetText && shape is TextShape) {
            shape.label.textProperty().bind(widget.textProperty())
            shape.label.strokeProperty().bind(widget.colourProperty())
        }

        widget.xProperty().addListener { _, _, _ ->
            record(ChangeType.CHANGE, widget)
        }

        widget.yProperty().addListener { _, _, _ ->
            record(ChangeType.CHANGE, widget)
        }

        add(widget)
        pane.children.add(shape)
    }

    fun destroy(widget: Widget) {
        remove(widget)
        for (shape in pane.children) {
            if (shape is WidgetShape && shape.identifier == widget.identifier) {
                pane.children.remove(shape)
                break
            }
        }
    }

    fun record(type: ChangeType, widget: Widget) {
        action.record(type, widget)
    }

    private fun recordSingle(type: ChangeType, widget: Widget) {
        action.addSingle(type, widget)
    }

}