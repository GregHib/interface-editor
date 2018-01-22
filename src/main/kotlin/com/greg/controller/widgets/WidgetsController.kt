package src.com.greg.controller.widgets

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.paint.Color
import javafx.scene.shape.Shape
import src.com.greg.controller.ActionController
import src.com.greg.controller.ChangeType
import src.com.greg.model.widgets.Widget
import src.com.greg.model.widgets.WidgetRectangle
import src.com.greg.model.widgets.WidgetText
import src.com.greg.model.widgets.WidgetsModel
import src.com.greg.view.WidgetShape
import src.com.greg.view.widgets.RectangleShape
import src.com.greg.view.widgets.TextShape
import tornadofx.Controller

class WidgetsController : Controller() {
    companion object {
        val widgets = WidgetsModel()
    }
//    private val hierarchy: HierarchyController by inject(DefaultScope)
    private val action = ActionController(this)

//    val panels = PanelController(this)

//    val refresh = RefreshManager(panels, hierarchy)

    fun add(widget: Widget) {
        recordSingle(ChangeType.ADD, widget)
        widgets.add(widget)
    }

    fun remove(widget: Widget) {
        recordSingle(ChangeType.REMOVE, widget)
        widgets.remove(widget)
    }

    inline fun forSelected(action: (Widget) -> Unit) {
        getSelection().forEach { element ->
            action(element)
        }
    }

    fun getAll(): ObservableList<Widget> {
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
            remove(widget)
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
        println("Refresh")
//        refresh.request()
    }

    fun record(type: ChangeType, widget: Widget) {
        action.record(type, widget)
    }

    private fun recordSingle(type: ChangeType, widget: Widget) {
        action.addSingle(type, widget)
    }

    fun connect(widget: Widget, shape: WidgetShape) {
        widget.selectedProperty().addListener { _, oldValue, newValue ->
            if (oldValue != newValue) {
                shape.outline.toFront()
                shape.outline.stroke = if (newValue) Color.RED else Color.WHITE
                requestRefresh()
            }
        }

        //Position
        widget.xProperty().bindBidirectional(shape.translateXProperty())
        widget.yProperty().bindBidirectional(shape.translateYProperty())
        widget.xProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
        widget.yProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }

        //Appearance
        shape.outline.widthProperty().bind(widget.widthProperty())
        shape.outline.heightProperty().bind(widget.heightProperty())

        if(widget is WidgetRectangle && shape is RectangleShape) {
            shape.rectangle.fillProperty().bind(widget.fillProperty())
            shape.rectangle.strokeProperty().bind(widget.strokeProperty())
        } else if(widget is WidgetText && shape is TextShape) {
            shape.label.textProperty().bind(widget.text)
            shape.label.strokeProperty().bind(widget.colour)
        }
    }

}