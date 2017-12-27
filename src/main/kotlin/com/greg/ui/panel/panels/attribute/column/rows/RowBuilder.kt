package com.greg.ui.panel.panels.attribute.column.rows

import com.greg.ui.panel.panels.attribute.AttributeType
import com.greg.ui.panel.panels.element.ElementType
import com.greg.ui.panel.panels.element.elements.ColourPickerElement
import com.greg.ui.panel.panels.element.elements.NumberFieldElement
import com.greg.ui.panel.panels.element.elements.TextFieldElement
import javafx.scene.paint.Color

class RowBuilder {

    constructor(title: String? = null) {
        addTitle(title)
        addSpace()
    }

    val componentList = mutableListOf<ElementType>()
    val components = mutableListOf<Any?>()

    fun addTitle(title: String?) {
        add(ElementType.TITLE, title)
    }

    fun addSpace() {
        add(ElementType.SPACE)
    }

    fun addTextField(default: String?) {
        add(ElementType.ATTRIBUTE, TextFieldElement(default))
    }

    fun addNumberField(default: Int?) {
        add(ElementType.ATTRIBUTE, NumberFieldElement(default))
    }

    fun addColourPicker(default: Color?) {
        add(ElementType.ATTRIBUTE, ColourPickerElement(default))
    }

    private fun add(type: ElementType, component: Any? = null) {
        componentList.add(type)
        components.add(component)
    }

    fun build() : Row {
        return Row(this)
    }

    fun addAttribute(type: AttributeType, value: Any) {
        when (type) {
            AttributeType.TEXT_FIELD -> addTextField(value.toString())
            AttributeType.COLOUR_PICKER -> addColourPicker(value as Color)
            AttributeType.NUMBER_FIELD -> addNumberField((value as Double).toInt())
        }
    }
}