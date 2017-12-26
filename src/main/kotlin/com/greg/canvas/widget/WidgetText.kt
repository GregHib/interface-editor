package com.greg.canvas.widget

import com.greg.panels.attributes.AttributeType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text

class WidgetText : AttributeWidget {

    private val text: Text

    constructor(string: String? = Settings.get(SettingsKey.DEFAULT_TEXT_MESSAGE), colour: Color? = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)) {
        text = Text(string)
        text.stroke = colour as Paint
        text.textOrigin = VPos.TOP

        attributes.addProperty("Message", "textProperty", AttributeType.TEXT_FIELD)
        attributes.addProperty("Text Colour", "strokeProperty", AttributeType.COLOUR_PICKER)

    }

    override fun getNode(): Node {
        return text
    }
}
