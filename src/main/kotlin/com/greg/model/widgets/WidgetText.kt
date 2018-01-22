package src.com.greg.model.widgets

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.paint.Color

class WidgetText(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    var text = SimpleStringProperty(this, "text", "Text")
    var colour = SimpleObjectProperty(this, "colour", Color.BLACK)

    init {
        properties.add(text)
        properties.add(colour)
    }
}