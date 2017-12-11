package com.greg.panels.attributes.parts

import com.greg.panels.attributes.Linkable
import com.greg.panels.attributes.types.AttributeSpacer
import com.greg.panels.attributes.types.ColourPickerAttribute
import com.greg.panels.attributes.types.NumberFieldAttribute
import com.greg.panels.attributes.types.TextFieldAttribute
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

class AttributeRow(vararg elements: Node) : HBox() {

    var linkableList = mutableListOf<Linkable>()

    companion object {
        fun createColourPicker(title: String, default: Color): AttributeRow {
            return createRow(title, ColourPickerAttribute(default))
        }

        fun createTextField(title: String, default: String?): AttributeRow {
            return createRow(title, TextFieldAttribute(default))
        }

        fun createNumberField(title: String, default: Int?): AttributeRow {
            return createRow(title, NumberFieldAttribute(default))
        }

        private fun createRow(title: String, default: Linkable): AttributeRow {
            val row = AttributeRow(Label(title), AttributeSpacer())
            row.add(default)
            return row
        }
    }

    fun add(vararg elements: Linkable) {
        for (element in elements) {
            if (element !is Node)
                throw IllegalArgumentException("Invalid element added to attribute row")
            else
                children.add(element)
        }
        linkableList.addAll(elements)
    }

    init {
        prefWidth = 280.0
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        alignment = Pos.CENTER
        children.addAll(elements)
    }
}