package com.greg.canvas.widget

import com.greg.panels.attributes.parts.pane.AttributePaneType
import com.greg.panels.attributes.Attribute

interface WidgetInterface {
    fun getProperties(type: AttributePaneType): List<Attribute>?
}