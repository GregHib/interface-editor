package com.greg.controller.model

import com.greg.controller.controller.Memento
import com.greg.controller.controller.MementoBuilder
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.movement.StartPoint
import com.greg.ui.canvas.widget.type.WidgetType
import javafx.beans.property.BooleanProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty

open class Widget(builder: WidgetBuilder, id: Int) {

    val type: WidgetType = builder.type
    val identifier = id
    val name: String = type::class.simpleName.toString()
    var start = StartPoint(0, 0)

    private var locked: BooleanProperty? = null
    private var selected: BooleanProperty? = null
    private var hidden: BooleanProperty? = null

    private var x: IntegerProperty? = null
    private var y: IntegerProperty? = null
    private var width: IntegerProperty? = null
    private var height: IntegerProperty? = null

    fun setLocked(value: Boolean) { lockedProperty().set(value) }
    fun isLocked(): Boolean { return lockedProperty().get() }
    fun lockedProperty(): BooleanProperty {
        if (locked == null)
            locked = SimpleBooleanProperty(this, "locked", false)

        return locked!!
    }

    fun isHidden(): Boolean { return hiddenProperty().get() }
    fun setHidden(value: Boolean) { hiddenProperty().set(value) }
    fun hiddenProperty(): BooleanProperty {
        if(hidden == null)
            hidden = SimpleBooleanProperty(this, "hidden", false)

        return hidden!!
    }

    fun isSelected(): Boolean { return selectedProperty().get() }
    fun setSelected(value: Boolean) { selectedProperty().set(if(value && isLocked()) false else value) }
    fun selectedProperty(): BooleanProperty {
        if(selected == null)
            selected = SimpleBooleanProperty(this, "selected", false)

        return selected!!
    }

    fun getX(): Int { return xProperty().get() }
    fun setX(value: Int) { xProperty().set(value) }
    fun xProperty(): IntegerProperty {
        if(x == null)
            x = SimpleIntegerProperty(this, "x", Settings.getInt(SettingsKey.DEFAULT_POSITION_X))

        return x!!
    }

    fun getY(): Int { return yProperty().get() }
    fun setY(value: Int) { yProperty().set(value) }
    fun yProperty(): IntegerProperty {
        if(y == null)
            y = SimpleIntegerProperty(this, "y", Settings.getInt(SettingsKey.DEFAULT_POSITION_Y))

        return y!!
    }

    fun getWidth(): Int { return widthProperty().get() }
    fun setWidth(value: Int) { widthProperty().set(value) }
    fun widthProperty(): IntegerProperty {
        if(width == null)
            width = SimpleIntegerProperty(this, "width", Settings.getInt(SettingsKey.DEFAULT_RECTANGLE_WIDTH))

        return width!!
    }

    fun getHeight(): Int { return heightProperty().get() }
    fun setHeight(value: Int) { heightProperty().set(value) }
    fun heightProperty(): IntegerProperty {
        if(height == null)
            height = SimpleIntegerProperty(this, "height", Settings.getInt(SettingsKey.DEFAULT_RECTANGLE_HEIGHT))

        return height!!
    }

    fun getMemento() : Memento {
        return MementoBuilder(this).build()
    }

    fun restore(memento: Memento) {
        val type = memento.type

        setX(memento.values.removeAt(0).toString().toInt())
        setY(memento.values.removeAt(0).toString().toInt())
        setWidth(memento.values.removeAt(0).toString().toInt())
        setHeight(memento.values.removeAt(0).toString().toInt())
        println("Rest ${memento.values}")
    }
}