package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.parts.pane.AttributePaneType

interface WidgetInterface {

    fun getAttributes(type: AttributePaneType): List<Attribute>?

}