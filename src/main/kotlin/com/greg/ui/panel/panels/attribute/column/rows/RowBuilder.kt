package com.greg.ui.panel.panels.attribute.column.rows

import com.greg.ui.panel.panels.attribute.AttributeType
import com.greg.ui.panel.panels.element.ElementType
import com.greg.ui.panel.panels.element.elements.ColourPickerElement
import com.greg.ui.panel.panels.element.elements.NumberFieldElement
import com.greg.ui.panel.panels.element.elements.TextFieldElement
import javafx.scene.paint.Color

class RowBuilder(title: String? = null) {

    val componentList = mutableListOf<ElementType>()
    val components = mutableListOf<Any?>()

    init {
        addTitle(title)
        addSpace()
    }

    fun addAttribute(type: AttributeType, value: Any) {
        when (type) {
            AttributeType.TEXT_FIELD -> addTextField(value.toString())
            AttributeType.COLOUR_PICKER -> addColourPicker(value as Color)
            AttributeType.NUMBER_FIELD -> addNumberField(value as Int)
        }
    }

    private fun addTitle(title: String?) {
        add(ElementType.TITLE, title)
    }

    private fun addSpace() {
        add(ElementType.SPACE)
    }

    private fun addTextField(default: String?) {
        add(ElementType.ATTRIBUTE, TextFieldElement(default))
    }

    private fun addNumberField(default: Int?) {
        add(ElementType.ATTRIBUTE, NumberFieldElement(default))
    }

    private fun addColourPicker(default: Color?) {
        add(ElementType.ATTRIBUTE, ColourPickerElement(default))
    }

    private fun add(type: ElementType, component: Any? = null) {
        componentList.add(type)
        components.add(component)
    }

    fun build() : Row {
        return Row(this)
    }
}