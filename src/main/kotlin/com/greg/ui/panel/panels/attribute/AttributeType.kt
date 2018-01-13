package com.greg.ui.panel.panels.attribute

import javafx.scene.paint.Color

enum class AttributeType {
    TEXT_FIELD,
    COLOUR_PICKER,
    NUMBER_FIELD;

    fun convert(value: Any?): Any {
        return when (this) {
            TEXT_FIELD -> value.toString()
            COLOUR_PICKER -> Color.valueOf(value.toString())
            NUMBER_FIELD -> value.toString().toDouble().toInt()
        }
    }
}