package com.greg.properties.attributes

import com.greg.properties.attributes.types.ColourPickerProperty
import com.greg.properties.attributes.types.PropertySpacer
import com.greg.properties.attributes.types.TextFieldProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

class PropertyRow : HBox {

    companion object {
        fun createColourPicker(title: String, default: Color): PropertyRow {
            return PropertyRow(Label(title), PropertySpacer(), ColourPickerProperty(default))
        }

        fun createTextField(title: String, default: String?): PropertyRow {
            return PropertyRow(Label(title), PropertySpacer(), TextFieldProperty(default))
        }

        fun createRowGroup(title: String, vararg propertyRows: PropertyRow): PropertyGroup {
            var group = PropertyGroup(title)
            group.add(*propertyRows)
            return group
        }
    }

    constructor(vararg elements: Node) {
        prefWidth = 280.0
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        alignment = Pos.CENTER
        children.addAll(elements)
    }
}