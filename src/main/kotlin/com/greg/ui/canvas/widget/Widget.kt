package com.greg.ui.canvas.widget

import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.panel.panels.attribute.Attribute
import com.greg.ui.panel.panels.PanelType
import javafx.scene.Node

interface Widget {

    fun getAttributes(type: PanelType): List<Attribute>?

    fun getClass(): WidgetFacade

    fun getNode(): Node
}