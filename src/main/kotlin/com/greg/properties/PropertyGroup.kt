package com.greg.properties

import com.greg.canvas.widget.Widget
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.VBox
import kotlin.reflect.KClass

class PropertyGroup : VBox {

    var title = Label("Title")
    var widgetClass: KClass<out Widget>?
    val properties = mutableListOf<PropertyRow>()

    constructor(text: String?, widget: KClass<out Widget>?) {
        prefWidth = 280.0
        this.widgetClass = widget


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

    fun add(vararg propertyRow: PropertyRow) {
        properties.addAll(propertyRow)
        children.addAll(propertyRow)
    }

    fun size(): Int {
        return properties.size
    }
}
