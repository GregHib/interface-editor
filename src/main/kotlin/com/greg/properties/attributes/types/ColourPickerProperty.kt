package com.greg.properties.attributes.types

import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color



class ColourPickerProperty : ColorPicker {

    constructor(default: Color, accept: (colour: Color) -> Unit) {
        this.value = default

        // Action handler
        // ---------------------------------------------------------------
        setOnAction { accept(value) }
    }
}