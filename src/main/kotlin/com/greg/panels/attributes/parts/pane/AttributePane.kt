package com.greg.panels.attributes.parts.pane

import com.greg.panels.attributes.parts.AttributeGroup
import javafx.geometry.Insets
import javafx.scene.control.TitledPane
import javafx.scene.layout.AnchorPane

class AttributePane : TitledPane {

    var groups: List<AttributeGroup>? = null
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