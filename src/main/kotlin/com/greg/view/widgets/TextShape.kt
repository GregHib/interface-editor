package com.greg.view.widgets

import com.greg.model.settings.Settings
import javafx.geometry.VPos
import javafx.scene.text.Text
import com.greg.view.WidgetShape
import tornadofx.add

class TextShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {
    val label = Text(Settings.get(Settings.DEFAULT_TEXT_MESSAGE))

    init {
        label.stroke = Settings.getColour(Settings.DEFAULT_TEXT_COLOUR)
        label.textOrigin = VPos.TOP

        add(label)
    }

}