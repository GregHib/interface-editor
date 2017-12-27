package com.greg.ui.panel.panels.element.elements

import com.greg.ui.panel.panels.element.Element
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color


class ColourPickerElement(default: Color?) : ColorPicker(), Element {

    override var links: MutableList<(value : Any?) -> Unit> = mutableListOf()

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

    override fun refresh(value: Any?) {
        if(value != null)
            this.value = value as Color
    }
}