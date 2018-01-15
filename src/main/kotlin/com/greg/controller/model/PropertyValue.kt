package com.greg.controller.model

import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.panel.panels.PanelType
import javafx.beans.property.Property

data class PropertyValue(val property: Property<*>, val pane: PanelType, val type: WidgetType)