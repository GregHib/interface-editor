package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.pane.AttributePaneType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.shape.Rectangle

class WidgetRectangle : Rectangle, WidgetInterface {

    private var properties = mutableListOf<Attribute>()

    init {
        stroke = Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR)
        properties.add(Attribute("Background fill", "fillProperty", AttributeType.COLOUR_PICKER, this::class))
        properties.add(Attribute("Width", "widthProperty", AttributeType.NUMBER_FIELD, this::class))
        properties.add(Attribute("Height", "heightProperty", AttributeType.NUMBER_FIELD, this::class))
    }

    constructor(x: Double, y: Double, width: Double, height: Double) : super (x, y, width, height)

    constructor() : super(Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT))

    override fun getAttributes(type: AttributePaneType): List<Attribute>? {
        if(type == AttributePaneType.PROPERTIES)
            return properties
        return null
    }
}