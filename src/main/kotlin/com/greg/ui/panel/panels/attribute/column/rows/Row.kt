package com.greg.ui.panel.panels.attribute.column.rows

import com.greg.ui.panel.panels.element.Element
import com.greg.ui.panel.panels.element.ElementType
import com.greg.ui.panel.panels.element.elements.SpaceElement
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class Row(builder: RowBuilder) : HBox() {

    var elements = mutableListOf<Element>()

    init {
        prefWidth = 280.0
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        alignment = Pos.CENTER

        //Decode builder values
        builder.componentList
                .asSequence()
                .mapIndexedNotNull { index, type -> get(builder, type, index) }
                .forEach { children.add(it) }
    }

    private fun get(builder: RowBuilder, type: ElementType, index: Int): Node? {
        val attribute = builder.components[index]
        when (type) {
            ElementType.TITLE -> if (attribute is String) return Label(attribute)
            ElementType.SPACE -> return SpaceElement()
            ElementType.ATTRIBUTE -> if (attribute is Element) add(attribute)
        }
        return null
    }

    fun add(element: Element) {
        if (element !is Node)
            throw IllegalArgumentException("Invalid element added to attribute row")
        else
            children.add(element)
        elements.add(element)
    }
}