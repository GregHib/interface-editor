package com.greg.panels.attributes.parts

import com.greg.panels.attributes.Linkable
import com.greg.panels.attributes.RowComponentType
import com.greg.panels.attributes.types.AttributeSpacer
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class AttributeRow(builder: AttributeRowBuilder) : HBox() {

    var linkableList = mutableListOf<Linkable>()

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

    private fun get(builder: AttributeRowBuilder, type: RowComponentType, index: Int): Node? {
        val attribute = builder.components[index]
        when (type) {
            RowComponentType.TITLE -> if (attribute is String) return Label(attribute)
            RowComponentType.SPACE -> return AttributeSpacer()
            RowComponentType.ATTRIBUTE -> if (attribute is Linkable) add(attribute)
        }
        return null
    }

    fun add(element: Linkable) {
        if (element !is Node)
            throw IllegalArgumentException("Invalid element added to attribute row")
        else
            children.add(element)
        linkableList.add(element)
    }
}