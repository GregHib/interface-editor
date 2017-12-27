package com.greg.ui.canvas.widget.builder.data

import com.greg.ui.canvas.widget.Widget
import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.Attribute
import javafx.scene.Group

abstract class WidgetFacade : Group(), Widget {

    override fun getClass(): WidgetFacade {
        return this
    }

    var attributes = Attributes(getClass())

    override fun getAttributes(type: PanelType): List<Attribute>? {
        return attributes.getAttributes(type)
    }

}