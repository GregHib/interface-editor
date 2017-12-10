package com.greg.panels.attributes.parts

import com.greg.canvas.widget.WidgetInterface
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kotlin.reflect.KClass

class AttributeGroup : VBox {

    var widgetClass: KClass<out WidgetInterface>?
    val properties = mutableListOf<AttributeRow>()

    constructor(text: String?, widget: KClass<out WidgetInterface>?) {
        prefWidth = 278.0
        HBox.setHgrow(this, Priority.ALWAYS)
        this.widgetClass = widget


        //Separator
        var separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)

        //Title
        var title = Label(text)
        title.prefWidth = 278.0
        title.alignment = Pos.CENTER
        children.add(title)

        //Separator
        separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)
    }

    fun add(vararg attributeRow: AttributeRow) {
        properties.addAll(attributeRow)
        children.addAll(attributeRow)
    }

    fun size(): Int {
        return properties.size
    }
}
