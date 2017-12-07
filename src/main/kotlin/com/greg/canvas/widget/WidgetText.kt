package com.greg.canvas.widget

import com.greg.properties.Property
import com.greg.properties.PropertyGroup
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text
import kotlin.reflect.KMutableProperty1

class WidgetText : WidgetRectangle {

    private val widgetClass = WidgetText::class
    private val name: String = "Text"
    private var text: Text = Text()

    init {
        properties.add(Property("Message", "message", PropertyType.TEXT_FIELD, widgetClass))
        properties.add(Property("Text Colour", "stroke", PropertyType.COLOUR_PICKER, widgetClass))
    }

    //Width and height arguments will be changed as soon as message is set anyway.
    constructor(string: String?, colour: Color?) : super(Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), 0.0, 0.0) {
        message = string
        text.stroke = colour
        text.textOrigin = VPos.TOP
        this.children.add(text)
    }

    private fun refreshSize() {
        super.rectangle.width = text.layoutBounds.width
        super.rectangle.height = text.layoutBounds.height
    }

    private var message: String?
        get() {
            return text.text
        }
        set(value) {
            text.text = value
            refreshSize()
        }

    private var stroke: Paint
        get() {
            return text.stroke
        }
        set(value) {
            text.stroke = value
        }

    override fun refreshGroups(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

//        linkGroup(group, this)

        groups.remove(group)
        super.refreshGroups(groups)
    }

    override fun linkGroups(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        linkGroup(group, this)

        groups.remove(group)
        super.linkGroups(groups)
    }

    override fun handleReflection(property: Property): Any? {
        return (property.reflection as KMutableProperty1<WidgetText, *>).get(this)
    }

    override fun getGroups(): List<PropertyGroup> {
        var groups = mutableListOf<PropertyGroup>()

        groups.add(createPropertyGroup(name, widgetClass))

        groups.addAll(super.getGroups())

        return groups
    }

}
