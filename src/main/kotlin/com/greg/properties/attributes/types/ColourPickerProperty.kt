package com.greg.properties.attributes.types

import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color


class ColourPickerProperty : ColorPicker {

    var actions: MutableList<(colour: Color) -> Unit> = mutableListOf()

    constructor(default: Color) {
        this.value = default
        // Action handler
        // ---------------------------------------------------------------
        setOnAction {
            for(action in actions)
                action(value)
        }
    }

    fun link(action: (colour: Color) -> Unit) {
        this.actions.add(action)
    }
}