package com.greg.canvas.widget

import com.greg.properties.attributes.Property
import com.greg.properties.attributes.Property.Companion.createColourPicker
import com.greg.properties.attributes.PropertyGroup
import com.greg.properties.attributes.types.ColourPickerProperty
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle

open class WidgetRectangle(x: Double, y: Double, width: Double, height: Double): Widget() {

    private val name : String = "Rectangle"
    var rectangle = Rectangle(x, y, width, height)

    init {
        setSelection(Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
        this.children.add(rectangle)
    }

    override fun setSelection(colour: Paint?) {
        rectangle.stroke = colour
    }

    private var fill: Paint get() { return rectangle.fill } set(value) { rectangle.fill = value }


    private fun createGroup(): PropertyGroup {
        return Property.createGroup(name,
                createColourPicker("Background fill", fill as Color)
        )
    }

    override fun handleGroup(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        (group.properties[0].children[2] as ColourPickerProperty).link({ c -> fill = c })

        groups.remove(group)
    }

    override fun getGroup(): List<PropertyGroup> {
        var group = mutableListOf<PropertyGroup>()

        group.add(createGroup())

        return group
    }
}