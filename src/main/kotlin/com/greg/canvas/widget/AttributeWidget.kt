package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributesLists
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.scene.Group

abstract class AttributeWidget : Group(), WidgetInterface {

    override fun getClass(): AttributeWidget {
        return this
    }

    var attributes = AttributesLists(getClass())

    override fun getAttributes(type: AttributePaneType): List<Attribute>? {
        return attributes.getAttributes(type)
    }

}