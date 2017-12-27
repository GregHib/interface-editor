package com.greg.ui.panel.panels.attribute

import javafx.scene.paint.Color

enum class AttributeType {
    TEXT_FIELD,
    COLOUR_PICKER,
    NUMBER_FIELD;

    fun convert(value: String): Any {
        return when (this) {
            TEXT_FIELD -> value
            COLOUR_PICKER -> Color.valueOf(value)
            NUMBER_FIELD -> value.toDouble().toInt()
        }
    }
}