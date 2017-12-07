package com.greg.properties

import com.greg.canvas.widget.WidgetInterface
import com.greg.properties.types.ColourPickerProperty
import com.greg.properties.types.NumberFieldProperty
import com.greg.properties.types.PropertySpacer
import com.greg.properties.types.TextFieldProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import kotlin.reflect.KClass

class PropertyRow : HBox {

    var linkableList = mutableListOf<Linkable>()

    companion object {
        fun createColourPicker(title: String, default: Color): PropertyRow {
            var row = PropertyRow(Label(title), PropertySpacer())
            row.add(ColourPickerProperty(default))
            return row
        }

        fun createTextField(title: String, default: String?): PropertyRow {
            var row = PropertyRow(Label(title), PropertySpacer())
            row.add(TextFieldProperty(default))
            return row
        }

        fun createNumberField(title: String, default: Int?): PropertyRow {
            var row = PropertyRow(Label(title), PropertySpacer())
            println(default)
            row.add(NumberFieldProperty(default))
            return row
        }

        fun createRowGroup(title: String, widget: KClass<out WidgetInterface>, vararg propertyRows: PropertyRow): PropertyGroup {
            var group = PropertyGroup(title, widget)
            group.add(*propertyRows)
            return group
        }
    }

    constructor(vararg elements: Node) {
        prefWidth = 280.0
        padding = Insets(10.0, 10.0, 0.0, 10.0)
        alignment = Pos.CENTER
        children.addAll(elements)
    }

    fun add(vararg elements: Linkable) {
        for (element in elements) {
            if (element !is Node)
                throw IllegalArgumentException("Invalid element added to property row")
            else
                children.add(element)
        }
        linkableList.addAll(elements)
    }
}