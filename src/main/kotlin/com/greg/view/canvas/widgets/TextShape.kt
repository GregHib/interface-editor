package com.greg.view.canvas.widgets

import com.greg.model.settings.Settings
import javafx.scene.control.Label
import tornadofx.add

class TextShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {
    val label = Label(Settings.get(Settings.DEFAULT_TEXT_MESSAGE))

    init {
//        label.stroke = Settings.getColour(Settings.DEFAULT_TEXT_COLOUR)
//        label.textOrigin = VPos.TOP
        label.textFill = Settings.getColour(Settings.DEFAULT_TEXT_COLOUR)
//        label.alignment = Pos.TOP_CENTER

        label.isWrapText = true

        add(label)
    }

}