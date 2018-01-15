package com.greg.controller.model

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.widget.type.WidgetType
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var fill: ObjectProperty<Color>? = null
    private var stroke: ObjectProperty<Color>? = null

    init {
        properties.add(fillProperty(), WidgetType.RECTANGLE)
        properties.add(strokeProperty(), WidgetType.RECTANGLE)
    }

    fun setFill(value: Color) { fillProperty().set(value) }
    fun getFill(): Color { return fillProperty().get() }
    fun fillProperty(): ObjectProperty<Color> {
        if (fill == null)
            fill = SimpleObjectProperty<Color>(this, "fill", Settings.getColour(SettingsKey.DEFAULT_RECTANGLE_FILL_COLOUR))

        return fill!!
    }

    fun setStroke(value: Color) { strokeProperty().set(value) }
    fun getStroke(): Color { return strokeProperty().get() }
    fun strokeProperty(): ObjectProperty<Color> {
        if (stroke == null)
            stroke = SimpleObjectProperty<Color>(this, "stroke", Settings.getColour(SettingsKey.DEFAULT_RECTANGLE_STROKE_COLOUR))

        return stroke!!
    }
}