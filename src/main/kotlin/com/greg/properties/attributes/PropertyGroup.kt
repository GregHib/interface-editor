package com.greg.properties.attributes

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.VBox

class PropertyGroup : VBox {

    var title = Label("Title")

    val properties = mutableListOf<Property>()

    constructor(text: String?) {
        prefWidth = 280.0


        //Separator
        var separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)

        //Title
        title.text = text
        title.prefWidth = 280.0
        title.alignment = Pos.CENTER
        children.add(title)

        //Separator
        separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)
    }

    fun add(vararg property: Property) {
        properties.addAll(property)
        children.addAll(property)
    }
}
