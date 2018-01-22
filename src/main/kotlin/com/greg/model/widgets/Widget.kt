package com.greg.model.widgets

import com.greg.controller.canvas.DragContext
import com.greg.model.Properties
import com.greg.model.StartPoint
import com.greg.model.widgets.memento.Memento
import com.greg.model.widgets.memento.MementoBuilder
import com.greg.settings.Settings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty

open class Widget(builder: WidgetBuilder, id: Int) {

    val type: WidgetType = builder.type
    val identifier = id
    val name: String = type::class.simpleName.toString()
    val dragContext = DragContext()
    var start = StartPoint(0, 0)

    protected val properties = Properties()

    private var x: IntegerProperty? = null
    private var y: IntegerProperty? = null
    private var width: IntegerProperty? = null
    private var height: IntegerProperty? = null

    private var locked: BooleanProperty? = null
    private var selected: BooleanProperty? = null
    private var hidden: BooleanProperty? = null

    init {
        properties.add(xProperty(), "Layout")
        properties.add(yProperty(), "Layout")
        properties.add(widthProperty(), "Layout")
        properties.add(heightProperty(), "Layout")
        properties.add(lockedProperty())
        properties.add(selectedProperty())
        properties.add(hiddenProperty())
    }

    fun setLocked(value: Boolean) {
        lockedProperty().set(value)
    }
    fun isLocked(): Boolean {
        return lockedProperty().get()
    }
    fun lockedProperty(): BooleanProperty {
        if (locked == null)
            locked = SimpleBooleanProperty(this, "locked", false)

        return locked!!
    }

    fun isHidden(): Boolean {
        return hiddenProperty().get()
    }

    fun setHidden(value: Boolean) {
        hiddenProperty().set(value)
    }

    fun hiddenProperty(): BooleanProperty {
        if (hidden == null)
            hidden = SimpleBooleanProperty(this, "hidden", false)

        return hidden!!
    }

    fun isSelected(): Boolean {
        return selectedProperty().get()
    }

    fun setSelected(value: Boolean) {
        selectedProperty().set(if (value && isLocked()) false else value)
    }

    fun selectedProperty(): BooleanProperty {
        if (selected == null)
            selected = SimpleBooleanProperty(this, "selected", false)

        return selected!!
    }
    fun getX(): Int {
        return xProperty().get()
    }

    fun setX(value: Int) {
        xProperty().set(value)
    }

    fun xProperty(): IntegerProperty {
        if (x == null)
            x = SimpleIntegerProperty(this, "x", Settings.getInt(Settings.DEFAULT_POSITION_X))

        return x!!
    }

    fun getY(): Int {
        return yProperty().get()
    }

    fun setY(value: Int) {
        yProperty().set(value)
    }

    fun yProperty(): IntegerProperty {
        if (y == null)
            y = SimpleIntegerProperty(this, "y", Settings.getInt(Settings.DEFAULT_POSITION_Y))

        return y!!
    }

    fun getWidth(): Int {
        return widthProperty().get()
    }

    fun setWidth(value: Int) {
        widthProperty().set(value)
    }

    fun widthProperty(): IntegerProperty {
        if (width == null)
            width = SimpleIntegerProperty(this, "width", Settings.getInt(Settings.DEFAULT_RECTANGLE_WIDTH))

        return width!!
    }

    fun getHeight(): Int {
        return heightProperty().get()
    }

    fun setHeight(value: Int) {
        heightProperty().set(value)
    }

    fun heightProperty(): IntegerProperty {
        if (height == null)
            height = SimpleIntegerProperty(this, "height", Settings.getInt(Settings.DEFAULT_RECTANGLE_HEIGHT))

        return height!!
    }

    fun getMemento(): Memento {
        return MementoBuilder(this).build()
    }

    fun restore(memento: Memento) {
        for ((index, value) in properties.get().withIndex())
            value.property.value = memento.values[index].convert(value.property)
    }
}