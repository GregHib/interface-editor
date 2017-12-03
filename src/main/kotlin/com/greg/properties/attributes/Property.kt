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

class Property : HBox {

    companion object {
        fun createColourPicker(title: String, default: Color, accept: (colour: Color) -> Unit): Property {
            return Property(Label(title), PropertySpacer(), ColourPickerProperty(default, accept))
        }

        fun createTextField(title: String, default: String?, accept: (text: String) -> Unit): Property {
            return Property(Label(title), PropertySpacer(), TextFieldProperty(default, accept))
        }

        fun createGroup(title: String, vararg properties: Property): PropertyGroup {
            var group = PropertyGroup(title)
            group.add(*properties)
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