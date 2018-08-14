package com.greg.controller.widgets

import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.selection.InteractionController
import com.greg.model.cache.CacheController
import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetsList
import com.greg.model.widgets.type.*
import com.greg.view.canvas.CanvasView
import com.greg.view.canvas.widgets.*
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.text.TextAlignment
import tornadofx.Controller
import tornadofx.add
import tornadofx.observable
import tornadofx.onChange


class WidgetsController : Controller() {
    companion object {
        val widgets = WidgetsList()
        val selection = mutableListOf<Widget>().observable()
    }

    private val action = InteractionController(this)


    /**
     * Selection
     */
    inline fun forSelected(action: (Widget) -> Unit) {
        getSelection().forEach { element ->
            action(element)
        }
    }

    fun getSelection(): ObservableList<Widget> {
        return selection
    }

    fun hasSelection(): Boolean {
        return getSelection().isNotEmpty()
    }

    fun clearSelection() {
        val list = arrayListOf<Widget>()
        forAll { widget ->
            if (widget.isSelected()) {
                list.add(widget)
                widget.setSelected(false, false)
            }
        }
        WidgetsController.selection.removeAll(list)
    }

    fun selectAll() {
        val list = arrayListOf<Widget>()
        forAll { widget ->
            if (!widget.isSelected()) {
                list.add(widget)
                widget.setSelected(true, false)
            }
        }
        WidgetsController.selection.addAll(list)
    }

    fun deleteSelection() {
        val iterator = getSelection().iterator()
        while (iterator.hasNext()) {
            remove(iterator.next())
            iterator.remove()
        }
    }

    /**
     * Widgets
     */
    fun add(widget: Widget) {
        widgets.add(widget)
    }

    fun addAll(widget: Array<out Widget>) {
        widgets.addAll(*widget)
    }

    fun remove(widget: Widget) {
        widgets.remove(widget)
    }

    fun getAll(): ObservableList<Widget> {
        return widgets.get()
    }

    fun size(): Int {
        return widgets.size()
    }

    /*
     Controls
     */
    fun forAll(action: (Widget) -> Unit) {
        forEach(getAll(), action)
    }

    fun getAllIntersections(canvas: PannableCanvas, bounds: Bounds): List<Widget> {
        return getAll()
                .filter { widget -> !widget.isLocked() }
                .filter { widget ->
                    val canvasX = canvas.boundsInParent.minX
                    val scaleOffsetX = canvas.boundsInLocal.minX * canvas.scaleX
                    val widgetX = canvasX - scaleOffsetX + (widget.getX() * canvas.scaleX)

                    val canvasY = canvas.boundsInParent.minY
                    val scaleOffsetY = canvas.boundsInLocal.minY * canvas.scaleY
                    val widgetY = canvasY - scaleOffsetY + (widget.getY() * canvas.scaleY)

                    val widgetBounds = BoundingBox(widgetX, widgetY, widget.getWidth() * canvas.scaleX, widget.getHeight() * canvas.scaleY)

                    bounds.intersects(widgetBounds)
                }
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

    fun getShape(canvas: PannableCanvas, widget: Widget): WidgetShape? {
        return canvas.children
                .filterIsInstance<WidgetShape>()
                .firstOrNull { it.identifier == widget.identifier }
    }

    fun forEach(widgets: List<Widget>, action: (Widget) -> Unit) {
        widgets.forEach { widget ->
            if(widget is WidgetContainer)
                forEach(widget.getChildren(), action)
            action(widget)
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

    fun connect(widget: Widget, shape: WidgetShape, cache: CacheController, children: List<WidgetShape>?) {

        //Selection
        updateSelection(widget, shape, !widget.isSelected(), widget.isSelected())
        widget.selectedProperty().addListener { _, oldValue, newValue -> updateSelection(widget, shape, oldValue, newValue) }

        //Position
        shape.translateXProperty().bindBidirectional(widget.xProperty())
        shape.translateYProperty().bindBidirectional(widget.yProperty())

        //Appearance
        shape.outline.widthProperty().bindBidirectional(widget.widthProperty())
        shape.outline.heightProperty().bindBidirectional(widget.heightProperty())


        //Listener
        updateVisibility(shape, widget.isInvisible())
        widget.invisibleProperty().addListener { _, _, newValue -> updateVisibility(shape, newValue) }

        if(widget is WidgetContainer && shape is ContainerShape) {
            children?.forEach { shape.children.add(it) }
        } else if (widget is WidgetRectangle && shape is RectangleShape) {
            shape.updateColour(widget)
            val listener = ChangeListener<Any> { _, _, _ -> shape.updateColour(widget) }

            widget.hoveredProperty().addListener(listener)
            widget.filledProperty().addListener(listener)
            widget.defaultColourProperty().addListener(listener)
            widget.defaultHoverColourProperty().addListener(listener)
            widget.secondaryColourProperty().addListener(listener)
            widget.secondaryHoverColourProperty().addListener(listener)
        } else if (widget is WidgetText && shape is TextShape) {
            shape.updateColour(widget)
            shape.updateText(widget, cache = cache)

            var listener = ChangeListener<Any> { _, oldValue, newValue ->
                if(oldValue != newValue) {
                    shape.updateColour(widget)
                    shape.updateText(widget, cache = cache)
                }
            }

            widget.hoveredProperty().addListener(listener)
            widget.defaultColourProperty().addListener(listener)
            widget.defaultHoverColourProperty().addListener(listener)
            widget.secondaryColourProperty().addListener(listener)
            widget.secondaryHoverColourProperty().addListener(listener)

            listener = ChangeListener { _, oldValue, newValue ->
                if(oldValue != newValue)
                shape.updateText(widget, cache = cache)
            }

            widget.defaultTextProperty().addListener(listener)
            widget.secondaryTextProperty().addListener(listener)
            widget.widthProperty().addListener(listener)
            widget.heightProperty().addListener(listener)

            widget.fontIndexProperty().addListener(listener)
            widget.shadowProperty().addListener(listener)
            widget.centredProperty().addListener { _, oldValue, newValue ->
                if(oldValue != newValue) {
                    shape.label.textAlignment = if (newValue) TextAlignment.CENTER else TextAlignment.LEFT
                    shape.label.alignment = if (newValue) Pos.TOP_CENTER else Pos.TOP_LEFT
                    shape.updateText(widget, cache = cache)
                }
            }
        } else if (widget is WidgetSprite && shape is SpriteShape) {
            shape.spriteProperty().bind(widget.defaultSpriteProperty())
            shape.archiveProperty().bind(widget.defaultSpriteArchiveProperty())

            //Update archive values
            updateArchive(widget, widget.getDefaultSpriteArchive())
            shape.archiveProperty().addListener { _, oldValue, newValue ->
                if(oldValue != newValue)
                    updateArchive(widget, newValue)
            }
        }
    }

    private fun updateArchive(widget: WidgetSprite, newValue: String) {
        //Get the number of sprites in archive
        val archive = ArchiveMedia.getImage("$newValue.dat")//TODO the gnome hash isn't .dat? are all .dat?
        val size = (archive?.sprites?.size ?: 1) - 1

        //Limit the sprite index to archive size
        widget.setDefaultCap(IntRange(0, size))

        //If already on an index which is greater than archive index; reduce, otherwise set the same (refresh)
        widget.setDefaultSprite(if (widget.getDefaultSprite() >= size) size else widget.getDefaultSprite())
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