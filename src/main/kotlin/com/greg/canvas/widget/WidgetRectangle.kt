package com.greg.canvas.widget

import com.greg.properties.Property
import com.greg.properties.PropertyGroup
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import kotlin.reflect.KMutableProperty1

open class WidgetRectangle(x: Double, y: Double, width: Double, height: Double) : Widget() {

    private val widgetClass = WidgetRectangle::class
    private val name: String = "Rectangle"
    var rectangle = Rectangle(x, y, width, height)

    init {
        setSelection(Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
        properties.add(Property("Background fill", "fill", PropertyType.COLOUR_PICKER, widgetClass))
        properties.add(Property("Width", "width", PropertyType.NUMBER_FIELD, widgetClass))
        properties.add(Property("Height", "height", PropertyType.NUMBER_FIELD, widgetClass))
        properties.add(Property("Location X", "x", PropertyType.NUMBER_FIELD, widgetClass))
        properties.add(Property("Location Y", "y", PropertyType.NUMBER_FIELD, widgetClass))
        this.children.add(rectangle)
    }

    override fun setSelection(colour: Paint?) {
        rectangle.stroke = colour
    }

    private var fill: Paint
        get() {
            return rectangle.fill
        }
        set(value) {
            rectangle.fill = value
        }

    private var width: Double
        get() {
            return rectangle.width
        }
        set(value) {
            if(value > Settings.getDouble(SettingsKey.WIDGET_CANVAS_WIDTH))
                rectangle.height = Settings.getDouble(SettingsKey.WIDGET_CANVAS_WIDTH)
            else
                rectangle.width = value
        }

    private var height: Double
        get() {
            return rectangle.height
        }
        set(value) {
            if(value > Settings.getDouble(SettingsKey.WIDGET_CANVAS_HEIGHT))
                rectangle.height = Settings.getDouble(SettingsKey.WIDGET_CANVAS_HEIGHT)
            else
                rectangle.height = value
        }

    private var x: Double
        get() {
            return layoutX
        }
        set(value) {
            layoutX = value
        }

    private var y: Double
        get() {
            return layoutY
        }
        set(value) {
            layoutY = value
        }

    override fun handleReflection(property: Property): Any? {
        return (property.reflection as KMutableProperty1<WidgetRectangle, *>).get(this)
    }

    override fun refreshGroups(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        refreshGroup(group, this)

        groups.remove(group)
    }

    override fun linkGroups(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        linkGroup(group, this)

        groups.remove(group)
    }

    override fun getGroups(): List<PropertyGroup> {
        var groups = mutableListOf<PropertyGroup>()

        groups.add(createPropertyGroup(name, widgetClass))

        return groups
    }
}