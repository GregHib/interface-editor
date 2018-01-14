package com.greg.ui.canvas.widget.builder.data

import com.greg.ui.canvas.movement.StartPoint
import com.greg.ui.canvas.widget.Widget
import com.greg.ui.canvas.widget.builder.WidgetBuilder
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.paint.Color

abstract class WidgetData(builder: WidgetBuilder, id: Int) : WidgetFacade() {

    var components = mutableListOf<Widget>()
    var start: StartPoint? = null
    val identifier: Int = id
    private var locked: BooleanProperty? = null
    private var selected: BooleanProperty? = null
    val name: String = builder.components.last()::class.simpleName.toString()

    init {
        //Add all the rest, default just rectangle
        for (component in builder.components)
            add(component)
    }

    fun setLocked(value: Boolean) { lockedProperty().set(value) }
    fun isLocked(): Boolean { return lockedProperty().get() }
    fun lockedProperty(): BooleanProperty {
        if (locked == null)
            locked = SimpleBooleanProperty(this, "locked", false)

        return locked!!
    }

    fun isSelected(): Boolean { return selectedProperty().get() }
    fun setSelected(value: Boolean) { selectedProperty().set(if(value && isLocked()) false else value) }
    fun selectedProperty(): BooleanProperty {
        if(selected == null)
            selected = SimpleBooleanProperty(this, "hasSelection", false)

        return selected!!
    }

    override fun getNode(): Node {
        return this
    }

    protected fun addToStart(component: Widget) {
        components.add(0, component)
    }

    protected fun add(component: WidgetFacade) {
        components.add(component)
        children.add(component.getNode())
    }

    fun setWidth(width: Double) {
        getRectangle().getRectangle().width = width
    }

    fun setHeight(height: Double) {
        getRectangle().getRectangle().height = height
    }

    fun setSelection(colour: Color?) {
        getRectangle().getRectangle().stroke = colour
    }

    fun getRectangle(): WidgetRectangle {
        return components[1] as WidgetRectangle
    }
}