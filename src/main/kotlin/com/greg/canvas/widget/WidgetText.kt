package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.pane.AttributePaneType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text

class WidgetText : Text, WidgetInterface {

    private var properties = mutableListOf<Attribute>()

    init {
        properties.add(Attribute("Message", "message", AttributeType.TEXT_FIELD, this::class))
        properties.add(Attribute("Text Colour", "strokeProperty", AttributeType.COLOUR_PICKER, this::class))
    }

    constructor(string: String? = Settings.get(SettingsKey.DEFAULT_TEXT_MESSAGE), colour: Color? = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)) : super(string) {
        stroke = colour as Paint
        textOrigin = VPos.TOP
    }


    private fun refreshSize() {
//        super.rectangle.width = text.layoutBounds.width
//        super.rectangle.height = text.layoutBounds.height
    }

    private var message: String?
        get() {
            return text
        }
        set(value) {
            text = value
            refreshSize()
        }

    override fun getAttributes(type: AttributePaneType): List<Attribute>? {
        if(type == AttributePaneType.PROPERTIES)
            return properties
        return null
    }

}
