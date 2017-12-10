package com.greg.canvas.widget

import com.greg.panels.attributes.AttributePaneType
import com.greg.properties.Property

interface WidgetInterface {
    fun getProperties(type: AttributePaneType): List<Property>?
}