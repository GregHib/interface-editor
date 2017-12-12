package com.greg.panels.attributes.parts

import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.RowComponentType
import com.greg.panels.attributes.types.ColourPickerAttribute
import com.greg.panels.attributes.types.NumberFieldAttribute
import com.greg.panels.attributes.types.TextFieldAttribute
import javafx.scene.paint.Color

class AttributeRowBuilder {

    constructor(title: String? = null) {
        addTitle(title)
        addSpace()
    }

    val componentList = mutableListOf<RowComponentType>()
    val components = mutableListOf<Any?>()

    fun addTitle(title: String?) {
        add(RowComponentType.TITLE, title)
    }

    fun addSpace() {
        add(RowComponentType.SPACE)
    }

    fun addTextField(default: String?) {
        add(RowComponentType.ATTRIBUTE, TextFieldAttribute(default))
    }

    fun addNumberField(default: Int?) {
        add(RowComponentType.ATTRIBUTE, NumberFieldAttribute(default))
    }

    fun addColourPicker(default: Color?) {
        add(RowComponentType.ATTRIBUTE, ColourPickerAttribute(default))
    }

    private fun add(type: RowComponentType, component: Any? = null) {
        componentList.add(type)
        components.add(component)
    }

    fun build() : AttributeRow {
        return AttributeRow(this)
    }

    fun addAttribute(type: AttributeType, value: Any) {
        when (type) {
            AttributeType.TEXT_FIELD -> addTextField(value.toString())
            AttributeType.COLOUR_PICKER -> addColourPicker(value as Color)
            AttributeType.NUMBER_FIELD -> addNumberField((value as Double).toInt())
        }
    }
}