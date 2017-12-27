package com.greg.ui.panel.panels.element.elements

import com.greg.ui.panel.panels.element.Element
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color


class ColourPickerElement(default: Color?) : ColorPicker(), Element {

    override var links: MutableList<(value : Any?) -> Unit> = mutableListOf()

    override fun refresh(value: Any?) {
        if(value != null)
            this.value = value as Color
    }

    override fun link(action: (value: Any?) -> Unit) {
        if(action !is (value: Color?) -> Unit)
            throw UnsupportedOperationException()
        this.links.add(action)
    }

    init {
        if(default != null)
            value = default

        // Action handler
        // ---------------------------------------------------------------
        setOnAction {
            for(action in links)
                action(value)
        }
    }
}