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

    val updateHierarchy = BoolProperty("updateHierarchy", false)

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

    /**
     * Removes children from selection which already have parents which are selected
     */
    fun deselectChildren() {
        deselect(*getSelection().filterIsInstance<WidgetContainer>().toTypedArray())
    }

    private fun deselect(vararg containers: WidgetContainer) {
        containers.forEach { container ->
            deselect(*container.getChildren().filterIsInstance<WidgetContainer>().toTypedArray())
            selection.removeAll(container.getChildren())
        }
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

    fun remove(widget: Widget): Boolean {
        return widgets.remove(widget)
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
        widget.selected.addListener { _, oldValue, newValue -> updateSelection(widget, shape, oldValue, newValue) }

        //Position
        shape.translateXProperty().bindBidirectional(widget.x)
        shape.translateYProperty().bindBidirectional(widget.y)

        //Appearance
        shape.outline.widthProperty().bindBidirectional(widget.width)
        shape.outline.heightProperty().bindBidirectional(widget.height)


        //Listener
        updateVisibility(shape, widget.isInvisible())
        widget.invisible.addListener { _, _, newValue -> updateVisibility(shape, newValue) }

        if (widget is WidgetContainer && shape is ContainerShape) {
            widget.getChildren().addListener(ListChangeListener<Widget> { change ->
                change.next()
                if(change.wasAdded()) {
                    val widgets = change.addedSubList.filterIsInstance<Widget>()
                    widgets.forEach { it.setParent(widget) }

                    if(change.to - 1 >= shape.group.children.size)
                        shape.group.children.addAll(create(widgets))
                    else
                        shape.group.children.addAll(change.to - 1, create(widgets))
                } else if(change.wasRemoved()) {
                    change.removed.forEach { it.setParent(null) }
                    shape.group.children.removeAll(shape.group.children.filterIsInstance<WidgetShape>().filter { shape -> change.removed.any { shape.identifier == it.identifier } })
                }

                updateHierarchy.set(true)
            })
            children?.forEach { shape.group.add(it) }
        } else if (widget is WidgetRectangle && shape is RectangleShape) {
            shape.flip = WidgetScripts.scriptStateChanged(widget)
            shape.updateColour(widget)
            val listener = ChangeListener<Any> { _, _, _ -> shape.updateColour(widget) }

            widget.hovered.addListener(listener)
            widget.filled.addListener(listener)
            widget.defaultColour.addListener(listener)
            widget.defaultHoverColour.addListener(listener)
            widget.secondaryColour.addListener(listener)
            widget.secondaryHoverColour.addListener(listener)
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

            widget.hovered.addListener(listener)
            widget.defaultColour.addListener(listener)
            widget.defaultHoverColour.addListener(listener)
            widget.secondaryColour.addListener(listener)
            widget.secondaryHoverColour.addListener(listener)

            listener = ChangeListener { _, oldValue, newValue ->
                if (oldValue != newValue)
                    shape.updateText(widget, cache)
            }

            widget.defaultText.addListener(listener)
            widget.secondaryText.addListener(listener)
            widget.width.addListener(listener)
            widget.height.addListener(listener)

            widget.fontIndex.addListener(listener)
            widget.shadow.addListener(listener)
            widget.centred.addListener { _, oldValue, newValue ->
                if (oldValue != newValue) {
                    shape.label.textAlignment = if (newValue) TextAlignment.CENTER else TextAlignment.LEFT
                    shape.label.alignment = if (newValue) Pos.TOP_CENTER else Pos.TOP_LEFT
                    shape.updateText(widget, cache)
                }
            }
        } else if (widget is WidgetSprite && shape is SpriteShape) {
            shape.flip = WidgetScripts.scriptStateChanged(widget)
            shape.defaultSpriteProperty().bind(widget.defaultSprite)
            shape.defaultArchiveProperty().bind(widget.defaultSpriteArchive)
            shape.secondarySpriteProperty().bind(widget.secondarySprite)
            shape.secondaryArchiveProperty().bind(widget.secondarySpriteArchive)

            //Update archive values
            shape.updateArchive(widget, widget.getDefaultSpriteArchive(), true)
            widget.defaultSpriteArchive.addListener { _, oldValue, newValue ->
                if (oldValue != newValue)
                    shape.updateArchive(widget, newValue, true)
            }
            shape.updateArchive(widget, widget.getSecondarySpriteArchive(), false)
            widget.secondarySpriteArchive.addListener { _, oldValue, newValue ->
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