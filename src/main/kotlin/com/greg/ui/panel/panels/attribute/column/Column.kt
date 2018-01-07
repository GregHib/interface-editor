package com.greg.ui.panel.panels.attribute.column

import com.greg.ui.canvas.widget.Widget
import com.greg.ui.panel.panels.attribute.column.rows.Row
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import tornadofx.View
import tornadofx.label
import tornadofx.separator
import tornadofx.vbox
import kotlin.reflect.KClass

class Column(title: String?, widget: KClass<out Widget>?, separator: Boolean = true) : View() {

    var widgetClass: KClass<out Widget>? = widget
    val rows = mutableListOf<Row>()

    fun add(row: Row) {
        rows.add(row)
        root.add(row)
    }

    override val root = vbox {
        prefWidth = 276.0
        label {
            this.text = title
            padding = Insets(5.0, 0.0, 5.0, 0.0)
            alignment = Pos.CENTER
            prefWidth = 276.0
        }

        if (separator)
            separator { orientation = Orientation.HORIZONTAL }
    }
}
