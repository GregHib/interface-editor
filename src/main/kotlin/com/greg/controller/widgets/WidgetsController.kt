package com.greg.controller.widgets

import com.greg.controller.selection.InteractionController
import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetsList
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetRectangle
import com.greg.model.widgets.type.WidgetSprite
import com.greg.model.widgets.type.WidgetText
import com.greg.view.canvas.CanvasView
import com.greg.view.canvas.widgets.RectangleShape
import com.greg.view.canvas.widgets.SpriteShape
import com.greg.view.canvas.widgets.TextShape
import com.greg.view.canvas.widgets.WidgetShape
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.Controller
import tornadofx.observable
import tornadofx.onChange


class WidgetsController : Controller() {
    companion object {
        val widgets = WidgetsList()
        val selection = mutableListOf<Widget>().observable()
    }

    private val action = InteractionController(this)

    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun addAll(widget: Array<out Widget>) {
        widgets.addAll(*widget)
    }

    fun remove(widget: Widget) {
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
        if (target is Node) {
            var parent = target.parent
            if (parent is WidgetShape)
                return parent
            else if (parent is Node) {
                parent = parent.parent
                if (parent is WidgetShape)
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

    fun cut() {
        action.copy()
        deleteSelection()
    }

    fun deleteAll() {
        val iterator = getAll().iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
    }

    fun deleteSelection() {
        val iterator = getSelection().iterator()
        while (iterator.hasNext()) {
            remove(iterator.next())
            iterator.remove()
        }
    }

    fun delete(identifier: Int) {
        val iterator = getAll().iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.identifier == identifier) {
                remove(next)
                return
            }
        }
    }

    fun paste() {
        action.paste()
    }

    fun copy() {
        action.copy()
    }

    fun connect(widget: Widget, shape: WidgetShape) {

        //Selection
        widget.selectedProperty().addListener { _, oldValue, newValue -> updateSelection(widget, shape, oldValue, newValue) }

        //Position
        shape.translateXProperty().bindBidirectional(widget.xProperty())
        shape.translateYProperty().bindBidirectional(widget.yProperty())

        //Appearance
        shape.outline.widthProperty().bindBidirectional(widget.widthProperty())
        shape.outline.heightProperty().bindBidirectional(widget.heightProperty())


        //Listener
        updateVisibility(shape, widget.isHidden())
        widget.hiddenProperty().addListener { _, _, newValue -> updateVisibility(shape, newValue) }

        if (widget is WidgetRectangle && shape is RectangleShape) {
            shape.updateColour(widget)
            val listener = ChangeListener<Any> { _, _, _ -> shape.updateColour(widget) }

            widget.hoveredProperty().addListener(listener)
            widget.filledProperty().addListener(listener)
            widget.defaultColourProperty().addListener(listener)
            widget.defaultHoverColourProperty().addListener(listener)
            widget.secondaryColourProperty().addListener(listener)
            widget.secondaryHoverColourProperty().addListener(listener)
        } else if (widget is WidgetText && shape is TextShape) {
            //Binds
            shape.label.textProperty().bind(widget.textProperty())
            //Both are needed for colour
            shape.label.textFillProperty().bind(widget.colourProperty())
        } else if (widget is WidgetSprite && shape is SpriteShape) {
            shape.spriteProperty().bind(widget.spriteProperty())
            shape.archiveProperty().bind(widget.archiveProperty())

            //Update archive values
            updateArchive(widget, widget.getArchive())
            shape.archiveProperty().addListener { _, _, newValue ->
                updateArchive(widget, newValue)
            }
        }
    }

    private fun updateArchive(widget: WidgetSprite, newValue: String) {
        //Get the number of sprites in archive
        val archive = ArchiveMedia.getImage("$newValue.dat")//TODO the gnome hash isn't .dat? are all .dat?
        val size = (archive?.sprites?.size ?: 1) - 1

        //Limit the sprite index to archive size
        widget.setCap(IntRange(0, size))

        //If already on an index which is greater than archive index; reduce, otherwise set the same (refresh)
        widget.setSprite(if (widget.getSprite() >= size) size else widget.getSprite())
    }

    private fun updateVisibility(shape: WidgetShape, newValue: Boolean) {
        shape.isVisible = !newValue
    }

    private fun updateSelection(widget: Widget, shape: WidgetShape, oldValue: Boolean, newValue: Boolean = oldValue) {
        if (oldValue != newValue) {
            shape.outline.toFront()
            shape.outline.stroke = Settings.getColour(if (newValue) Settings.SELECTION_STROKE_COLOUR else Settings.DEFAULT_STROKE_COLOUR)
        }

        if (widget.updateSelection) {
            if (newValue)
                selection.add(widget)
            else
                selection.remove(widget)
        }
    }

    fun start(canvas: CanvasView) {
        getAll().onChange {
            it.next()
            canvas.refresh(it)
        }
    }
}