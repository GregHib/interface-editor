package com.greg.panels.attributes.types

import com.greg.panels.attributes.Linkable
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color


class ColourPickerAttribute(default: Color) : ColorPicker(), Linkable {

    override var links: MutableList<(value : Any?) -> Unit> = mutableListOf()

    override fun refresh(value: Any?) {
        this.value = value as Color
    }

    override fun link(action: (value: Any?) -> Unit) {
        if(action !is (value: Color?) -> Unit)
            throw UnsupportedOperationException()
        this.links.add(action)
    }

    init {
        value = default

        // Action handler
        // ---------------------------------------------------------------
        setOnAction {
            for(action in links)
                action(value)
        }
    }
}