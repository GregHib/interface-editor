package com.greg.panels.attributes

import com.greg.properties.PropertyGroup
import javafx.geometry.Insets
import javafx.scene.control.TitledPane
import javafx.scene.layout.AnchorPane

class AttributePane : TitledPane {

    var groups: List<PropertyGroup>? = null
    var paneType: AttributePaneType

    constructor(title: String, type: AttributePaneType) {
        this.text = title
        this.paneType = type

        maxWidth = 280.0

        val pane = AnchorPane()
        pane.padding = Insets(0.0, 0.0, 0.0, 0.0)
        content = pane
    }

    fun getPane(): AnchorPane {
        return content as AnchorPane
    }

    fun getType(): AttributePaneType {
        return paneType
    }
}