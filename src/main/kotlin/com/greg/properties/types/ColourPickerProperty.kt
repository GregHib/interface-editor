package com.greg.properties.types

import com.greg.properties.Linkable
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color


class ColourPickerProperty : ColorPicker, Linkable {

    override var links: MutableList<(value : Any?) -> Unit> = mutableListOf()

    constructor(default: Color) {
        this.value = default
        // Action handler
        // ---------------------------------------------------------------
        setOnAction {
            for(action in links)
                action(value)
        }
    }

    override fun refresh(value: Any?) {
        this.value = value as Color
    }

    override fun link(action: (value: Any?) -> Unit) {
        if(action !is (value: Color?) -> Unit)
            throw UnsupportedOperationException()
        this.links.add(action)
    }
}