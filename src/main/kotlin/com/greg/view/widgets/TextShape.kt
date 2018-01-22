package src.com.greg.view.widgets

import com.sun.scenario.Settings
import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.text.Text
import src.com.greg.view.WidgetShape
import tornadofx.add

class TextShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {
    val label = Text("Text"/*Settings.get(SettingsKey.DEFAULT_TEXT_MESSAGE)*/)

    init {
        label.stroke = Color.BLACK//Settings.getColour(SettingsKey.DEFAULT_TEXT_COLOUR)
        label.textOrigin = VPos.TOP

        add(label)
    }

}