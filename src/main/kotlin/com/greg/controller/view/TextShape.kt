package com.greg.controller.view

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.geometry.VPos
import javafx.scene.text.Text
import tornadofx.add

class TextShape(id: Int, x: Int, y: Int, width: Int, height: Int) : WidgetShape(id, x, y, width, height) {
    val label = Text(Settings.get(SettingsKey.DEFAULT_TEXT_MESSAGE))

    init {
        label.stroke = Settings.getColour(SettingsKey.DEFAULT_TEXT_COLOUR)
        label.textOrigin = VPos.TOP

        add(label)
    }

}