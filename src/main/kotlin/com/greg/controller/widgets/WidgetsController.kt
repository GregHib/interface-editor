package com.greg.controller.widgets

import com.greg.controller.actions.ActionController
import com.greg.controller.actions.ChangeType
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetsList
import com.greg.model.widgets.type.*
import com.greg.view.canvas.widgets.*
import com.greg.view.sprites.SpriteController
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.shape.Shape
import tornadofx.Controller
import tornadofx.observable


class WidgetsController : Controller() {
    companion object {
        val widgets = WidgetsList()
        val selection = mutableListOf<Widget>().observable()
    }
//    private val hierarchy: HierarchyController by inject(DefaultScope)
    val action = ActionController(this)

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

    fun getSelection(): ObservableList<Widget> {
        return selection
    }

    fun hasSelection(): Boolean {
        return getSelection().isNotEmpty()
    }

    fun size(): Int {
        return widgets.size()
    }

    fun getWidget(target: EventTarget?): Widget? {
        val shape = getShape(target) ?: return null
        return widgets.get(shape)
    }

    fun getWidget(target: WidgetShape): Widget? {
        return widgets.get(target)
    }

    fun getShape(target: EventTarget?): WidgetShape? {
        if (target is Shape) {
            var parent = target.parent
            if (parent is WidgetShape)
                return parent
            else if(parent is Node) {
                parent = parent.parent
                if(parent is WidgetShape)
                    return parent
            }
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
        val iterator = getSelection().iterator()
        while(iterator.hasNext()) {
            remove(iterator.next())
            iterator.remove()
        }
    }

    fun delete(identifier: Int) {
        val iterator = getAll().iterator()
        while(iterator.hasNext()) {
            val next = iterator.next()
            if(next.identifier == identifier) {
                remove(next)
                return
            }
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
                shape.outline.stroke = Settings.getColour(if (newValue) Settings.SELECTION_STROKE_COLOUR else Settings.DEFAULT_STROKE_COLOUR)

                if(newValue)
                    selection.add(widget)
                else
                    selection.remove(widget)
            }
        }

        //Binds

        //Position
        widget.xProperty().bindBidirectional(shape.translateXProperty())
        widget.yProperty().bindBidirectional(shape.translateYProperty())
        //Appearance
        shape.outline.widthProperty().bindBidirectional(widget.widthProperty())
        shape.outline.heightProperty().bindBidirectional(widget.heightProperty())


        //Listener
        widget.xProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
        widget.yProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
        widget.widthProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
        widget.heightProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
        widget.lockedProperty().addListener { _, _, _ -> recordSingle(ChangeType.CHANGE, widget) }
        widget.hiddenProperty().addListener { _, _, newValue ->
            shape.isVisible = !newValue
            recordSingle(ChangeType.CHANGE, widget)
        }

        if(widget is WidgetRectangle && shape is RectangleShape) {
            shape.rectangle.fillProperty().bind(widget.fillProperty())
            shape.rectangle.strokeProperty().bind(widget.strokeProperty())

            //Records
            shape.rectangle.fillProperty().addListener { _, _, _ -> recordSingle(ChangeType.CHANGE, widget) }
            shape.rectangle.strokeProperty().addListener { _, _, _ -> recordSingle(ChangeType.CHANGE, widget) }
        } else if(widget is WidgetText && shape is TextShape) {
            //Binds
            shape.label.textProperty().bind(widget.text)
            //Both are needed for colour
            shape.label.textFillProperty().bind(widget.colour)

            //Records
            shape.label.textProperty().addListener { _, _, _ -> record(ChangeType.CHANGE, widget) }
            shape.label.textFillProperty().addListener { _, _, _ -> recordSingle(ChangeType.CHANGE, widget) }
        } else if(widget is WidgetSprite && shape is SpriteShape) {
            shape.spriteProperty().bind(widget.spriteProperty())


            if(widget is WidgetCacheSprite && shape is CacheSpriteShape) {
                shape.archiveProperty().bind(widget.archiveProperty())

                //Every time widget archive is changed
                shape.archiveProperty().addListener { _, _, newValue ->
                    //Get the number of sprites in archive
                    val archive = SpriteController.getArchive("$newValue.dat")//TODO the gnome hash isn't .dat? are all .dat?
                    var size = archive?.sprites?.size ?: 1
                    size -= 1

                    //Limit the sprite index to archive size
                    widget.setCap(IntRange(0, size))

                    //If already on an index which is greater than archive index; reduce, otherwise set the same (refresh)
                    widget.setSprite(if(widget.getSprite() >= size) size else widget.getSprite())
                }
            }
        }
    }
}