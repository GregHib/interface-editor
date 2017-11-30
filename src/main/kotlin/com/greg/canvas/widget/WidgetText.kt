package com.greg.canvas.widget

import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.text.Text

class WidgetText : WidgetRectangle {

    private var text: Text

    constructor(string: String?, colour: Color) : super(0.0, 0.0, 0.0, 0.0) {
        text = Text(string)
        text.stroke = colour

        super.rectangle.x += 0.5
        super.rectangle.y += 0.5
        super.rectangle.width = text.layoutBounds.width
        super.rectangle.height = text.layoutBounds.height
        this.children.add(text)
    }

    var textOrigin: VPos
        get() {
            return text.textOrigin
        }
        set(value) {
            text.textOrigin = value
        }

}
