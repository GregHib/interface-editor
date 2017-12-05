package com.greg.canvas.widget

import com.greg.properties.PropertyType
import com.greg.properties.attributes.Property
import com.greg.properties.attributes.PropertyGroup
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text
import kotlin.reflect.KMutableProperty1

class WidgetText : WidgetRectangle {

    //TODO FIX having all properties on all parents https://gyazo.com/b720f9f038c94619bcd0886318c69780
    init {
        val start = System.currentTimeMillis()
        properties.add(Property("Message", "message", PropertyType.TEXT_FIELD, WidgetText::class))
        properties.add(Property("Text Colour", "stroke", PropertyType.COLOUR_PICKER, WidgetText::class))
        println("Delay: ${System.currentTimeMillis() - start}ms")
    }

    private val name: String = "Text"
    private var text: Text = Text()

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

    override fun handleGroup(groups: MutableList<PropertyGroup>) {
        val group = groups.first()

        linkGroup(group, this)

        groups.remove(group)
        super.handleGroup(groups)
    }

    override fun handleReflection(property: Property): Any? {
        return (property.reflection as KMutableProperty1<WidgetText, *>).get(this)
    }

    override fun getGroup(): List<PropertyGroup> {
        var groups = mutableListOf<PropertyGroup>()

        groups.add(createPropertyGroup(name))

        groups.addAll(super.getGroup())

        return groups
    }

}
