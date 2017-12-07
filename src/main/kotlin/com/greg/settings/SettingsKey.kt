package com.greg.settings

import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

enum class SettingsKey(var default: Any) {
    CANCEL_ON_DEFOCUS(false),
    ACCEPT_KEY_CODE(KeyCode.ENTER.ordinal),
    CANCEL_KEY_CODE(KeyCode.ESCAPE.ordinal),
    SELECTION_STROKE_COLOUR(Color.RED.toString()),
    DEFAULT_STROKE_COLOUR(Color.WHITE.toString()),
    DEFAULT_TEXT_MESSAGE("Text"),
    DEFAULT_TEXT_COLOUR(Color.WHITE.toString()),
    DEFAULT_POSITION_X(0.5),
    DEFAULT_POSITION_Y(0.5),
    DEFAULT_RECTANGLE_WIDTH(50.0),
    DEFAULT_RECTANGLE_HEIGHT(50.0),
    WIDGET_CANVAS_WIDTH(765.0),
    WIDGET_CANVAS_HEIGHT(503.0),
    ;

    val key: String = this.name
}