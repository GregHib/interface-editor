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

class AttributeGroup(text: String?, widget: KClass<out WidgetInterface>?) : VBox() {

    var widgetClass: KClass<out WidgetInterface>? = widget
    val rows = mutableListOf<AttributeRow>()

    fun add(row: AttributeRow) {
        rows.add(row)
        children.add(row)
    }

    init {
        prefWidth = 278.0
        HBox.setHgrow(this, Priority.ALWAYS)

        //Separator
        var separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)

        //Title
        val title = Label(text)
        title.prefWidth = 278.0
        title.alignment = Pos.CENTER
        children.add(title)

        //Separator
        separator = Separator()
        separator.orientation = Orientation.HORIZONTAL
        children.add(separator)
    }
}
