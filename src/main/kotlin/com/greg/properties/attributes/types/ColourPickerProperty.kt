package com.greg.properties.attributes.types

import com.greg.properties.attributes.Linkable
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

    override fun link(action: (value: Any?) -> Unit) {
        if(action !is (value: Color?) -> Unit)
            throw UnsupportedOperationException()
        this.links.add(action)
    }
}