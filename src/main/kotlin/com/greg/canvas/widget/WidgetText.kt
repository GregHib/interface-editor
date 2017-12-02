package com.greg.canvas.widget

import com.greg.properties.Attribute
import com.greg.properties.AttributeSpacer
import com.greg.properties.AttributeTextField
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Text

class WidgetText : WidgetRectangle {

    private var text: Text = Text()

    constructor(string: String?, colour: Color) : super(0.0, 0.0, 0.0, 0.0) {
        setText(string)
        text.stroke = colour

        super.rectangle.x += 0.5
        super.rectangle.y += 0.5
        this.children.add(text)
    }

    fun setText(string: String?) {
        text.text = string
        super.rectangle.width = text.layoutBounds.width
        super.rectangle.height = text.layoutBounds.height
    }

    fun getText(): String {
        return text.text
    }

    var textOrigin: VPos
        get() {
            return text.textOrigin
        }
        set(value) {
            text.textOrigin = value
        }

    fun getAttributes(): Attribute {
        val attribute = Attribute()
        var label = Label("Message")
        val spacer = AttributeSpacer()
        var text = AttributeTextField(this)
        attribute.children.addAll(label, spacer, text)
        return attribute
    }

}
