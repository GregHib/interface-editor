package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.scene.Node

interface WidgetInterface {

    fun getAttributes(type: AttributePaneType): List<Attribute>?

    fun getClass(): AttributeWidget

    fun getNode(): Node
}