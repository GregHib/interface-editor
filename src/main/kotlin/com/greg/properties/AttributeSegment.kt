package com.greg.properties

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.VBox

class AttributeSegment: VBox {

    var title = Label("Title")

    private val attributes = mutableListOf<Attribute>()

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

    fun add(attribute: Attribute) {
        attributes.add(attribute)
        children.add(attribute)
    }
}
