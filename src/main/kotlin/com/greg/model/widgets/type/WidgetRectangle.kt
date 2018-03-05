package com.greg.model.widgets.type

import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.scene.paint.Color

class WidgetRectangle(builder: WidgetBuilder, id: Int) : Widget(builder, id) {

    private var fill: ObjProperty<Color>? = null
    private var stroke: ObjProperty<Color>? = null

    init {
        properties.add(fillProperty())
        properties.add(strokeProperty())
    }

    fun setFill(value: Color) { fillProperty().set(value) }
    fun getFill(): Color { return fillProperty().get() }
    fun fillProperty(): ObjProperty<Color> {
        if (fill == null)
            fill = ObjProperty(this, "fill", Settings.getColour(Settings.DEFAULT_RECTANGLE_FILL_COLOUR))

        return fill!!
    }

    fun setStroke(value: Color) { strokeProperty().set(value) }
    fun getStroke(): Color { return strokeProperty().get() }
    fun strokeProperty(): ObjProperty<Color> {
        if (stroke == null)
            stroke = ObjProperty(this, "stroke", Settings.getColour(Settings.DEFAULT_RECTANGLE_STROKE_COLOUR))

        return stroke!!
    }
}