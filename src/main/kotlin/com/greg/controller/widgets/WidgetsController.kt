package com.greg.controller.widgets

import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.selection.InteractionController
import com.greg.model.cache.CacheController
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetsList
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.type.*
import com.greg.view.canvas.CanvasView
import com.greg.view.canvas.widgets.*
import javafx.beans.value.ChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Group
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

    val updateHierarchy = BoolProperty(this, "updateHierarchy", false)

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
        getSelection().forEach { remove(it) }
        getSelection().clear()
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

    fun get(): ObservableList<Widget> {
        return widgets.get()
    }

    fun size(): Int {
        return widgets.size()
    }

    /*
     Controls
     */
    fun forAll(action: (Widget) -> Unit) {
        getAll().forEach(action)
    }

    fun getAll(widgets: List<Widget> = get()): List<Widget> {
        val list = arrayListOf<Widget>()
        widgets.forEach { widget ->
            list.add(widget)
            if (widget is WidgetContainer)
                list.addAll(getAll(widget.getChildren()))
        }
        return list
    }

    private fun intersections(widget: Widget, x: Int, y: Int, action: (widget: Widget, x: Int, y: Int) -> Boolean): List<Widget> {
        val list = arrayListOf<Widget>()
        var x = x
        var y = y

        x += widget.getX()
        y += widget.getY()

        if (action(widget, x, y))
            list.add(widget)
        (widget as? WidgetContainer)?.getChildren()?.forEach { list.addAll(intersections(it, x, y, action)) }

        return list
    }

    fun getParentPosition(widget: WidgetShape): Point2D {
        var point = Point2D(widget.translateX, widget.translateY)
        var parent: Node = widget.parent

        while(parent is ContainerShape || parent is Group) {
            if(parent is ContainerShape)
                point = point.add(parent.translateX, parent.translateY)


            parent = parent.parent
        }

        return point
    }
    fun getAllIntersections(canvas: PannableCanvas, bounds: Bounds): List<Widget> {
        val list = arrayListOf<Widget>()
        get().forEach {
            list.addAll(
                    intersections(it, 0, 0) { widget, x, y ->
                        if (widget.isLocked())
                            false
                        else {
                            val canvasX = canvas.boundsInParent.minX
                            val scaleOffsetX = canvas.boundsInLocal.minX * canvas.scaleX
                            val widgetX = canvasX - scaleOffsetX + (x * canvas.scaleX)

                            val canvasY = canvas.boundsInParent.minY
                            val scaleOffsetY = canvas.boundsInLocal.minY * canvas.scaleY
                            val widgetY = canvasY - scaleOffsetY + (y * canvas.scaleY)

                            val widgetBounds = BoundingBox(widgetX, widgetY, widget.getWidth() * canvas.scaleX, widget.getHeight() * canvas.scaleY)

                            bounds.intersects(widgetBounds)
                        }
                    }
            )
        }
        return list
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
        canvas.children
                .filterIsInstance<WidgetShape>()
                .forEach {
                    val shape = getAllShapes(it, widget)
                    if(shape != null)
                        return shape
                }
        return null
    }

    private fun getAllShapes(shape: WidgetShape, widget: Widget): WidgetShape? {
        if (shape.identifier == widget.identifier)
            return shape
        (shape as? ContainerShape)?.group?.children
                ?.filterIsInstance<WidgetShape>()
                ?.forEach {
                    val child = getAllShapes(it, widget)
                    if(child != null)
                        return child
                }
        return null
    }

    fun clone() {
        action.clone()
    }

    fun cut() {
        action.copy()
        deleteSelection()
    }

    fun deleteAll() {
        val iterator = get().iterator()
        while (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        }
    }

    fun delete(identifier: Int) {
        forAll {
            if (it.identifier == identifier) {
                remove(it)
                return@forAll
            }
        }
    }

    fun paste() {
        action.paste()
    }

    fun copy() {
        action.copy()
    }

    fun connect(widget: Widget, shape: WidgetShape, cache: CacheController, children: List<WidgetShape>?, create: (widgets: List<Widget>) -> List<WidgetShape>) {

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

        if (widget is WidgetContainer && shape is ContainerShape) {
            widget.getChildren().addListener(ListChangeListener<Widget> { change ->
                change.next()
                if(change.wasAdded()) {
                    shape.group.children.addAll(change.to - 1, create(change.addedSubList.filterIsInstance<Widget>()))
                } else if(change.wasRemoved()) {
                    shape.group.children.removeAll(shape.group.children.filterIsInstance<WidgetShape>().filter { shape -> change.removed.any { shape.identifier == it.identifier } })
                }

                updateHierarchy.set(true)
            })
            children?.forEach { shape.group.add(it) }
        } else if (widget is WidgetRectangle && shape is RectangleShape) {
            shape.flip = WidgetScripts.scriptStateChanged(widget)
            shape.updateColour(widget)
            val listener = ChangeListener<Any> { _, _, _ -> shape.updateColour(widget) }

            widget.hoveredProperty().addListener(listener)
            widget.filledProperty().addListener(listener)
            widget.defaultColourProperty().addListener(listener)
            widget.defaultHoverColourProperty().addListener(listener)
            widget.secondaryColourProperty().addListener(listener)
            widget.secondaryHoverColourProperty().addListener(listener)
        } else if (widget is WidgetText && shape is TextShape) {
            shape.flip = WidgetScripts.scriptStateChanged(widget)
            shape.updateColour(widget)
            shape.updateText(widget, cache)

            var listener = ChangeListener<Any> { _, oldValue, newValue ->
                if (oldValue != newValue) {
                    shape.updateColour(widget)
                    shape.updateText(widget, cache)
                }
            }

            widget.hoveredProperty().addListener(listener)
            widget.defaultColourProperty().addListener(listener)
            widget.defaultHoverColourProperty().addListener(listener)
            widget.secondaryColourProperty().addListener(listener)
            widget.secondaryHoverColourProperty().addListener(listener)

            listener = ChangeListener { _, oldValue, newValue ->
                if (oldValue != newValue)
                    shape.updateText(widget, cache)
            }

            widget.defaultTextProperty().addListener(listener)
            widget.secondaryTextProperty().addListener(listener)
            widget.widthProperty().addListener(listener)
            widget.heightProperty().addListener(listener)

            widget.fontIndexProperty().addListener(listener)
            widget.shadowProperty().addListener(listener)
            widget.centredProperty().addListener { _, oldValue, newValue ->
                if (oldValue != newValue) {
                    shape.label.textAlignment = if (newValue) TextAlignment.CENTER else TextAlignment.LEFT
                    shape.label.alignment = if (newValue) Pos.TOP_CENTER else Pos.TOP_LEFT
                    shape.updateText(widget, cache)
                }
            }
        } else if (widget is WidgetSprite && shape is SpriteShape) {
            shape.flip = WidgetScripts.scriptStateChanged(widget)
            shape.defaultSpriteProperty().bind(widget.defaultSpriteProperty())
            shape.defaultArchiveProperty().bind(widget.defaultSpriteArchiveProperty())
            shape.secondarySpriteProperty().bind(widget.secondarySpriteProperty())
            shape.secondaryArchiveProperty().bind(widget.secondarySpriteArchiveProperty())

            //Update archive values
            shape.updateArchive(widget, widget.getDefaultSpriteArchive(), true)
            widget.defaultSpriteArchiveProperty().addListener { _, oldValue, newValue ->
                if (oldValue != newValue)
                    shape.updateArchive(widget, newValue, true)
            }
            shape.updateArchive(widget, widget.getSecondarySpriteArchive(), false)
            widget.secondarySpriteArchiveProperty().addListener { _, oldValue, newValue ->
                if (oldValue != newValue)
                    shape.updateArchive(widget, newValue, false)
            }
        }
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
        get().onChange {
            it.next()
            canvas.refresh(it)
        }
    }
}