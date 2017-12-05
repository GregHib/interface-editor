package com.greg.canvas.widget

import com.greg.properties.PropertyType
import com.greg.properties.attributes.Property
import com.greg.properties.attributes.PropertyGroup
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import kotlin.reflect.KMutableProperty1

open class WidgetRectangle(x: Double, y: Double, width: Double, height: Double): Widget() {

    private val name : String = "Rectangle"
    var rectangle = Rectangle(x, y, width, height)

    init {
        setSelection(Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
        properties.add(Property("Background fill", "fill", PropertyType.COLOUR_PICKER, WidgetRectangle::class))
        this.children.add(rectangle)
    }

    override fun setSelection(colour: Paint?) {
        rectangle.stroke = colour
    }

    private var fill: Paint get() { return rectangle.fill } set(value) { rectangle.fill = value }

    override fun handleReflection(property: Property): Any? {
        return (property.reflection as KMutableProperty1<WidgetRectangle, *>).get(this)
    }

    override fun handleGroup(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        linkGroup(group, this)

        groups.remove(group)
    }

    override fun getGroup(): List<PropertyGroup> {
        var groups = mutableListOf<PropertyGroup>()

        groups.add(createPropertyGroup(name))

        return groups
    }
}