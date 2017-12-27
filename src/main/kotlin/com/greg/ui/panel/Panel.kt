package com.greg.ui.panel

import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.column.Column
import javafx.geometry.Insets
import javafx.scene.control.TitledPane
import javafx.scene.layout.AnchorPane

class Panel(title: String, var type: PanelType) : TitledPane() {

    var groups: List<Column>? = null

    fun getPane(): AnchorPane {
        return content as AnchorPane
    }

    init {
        text = title
        maxWidth = 280.0
        val pane = AnchorPane()
        pane.padding = Insets(0.0, 0.0, 0.0, 0.0)
        content = pane
    }
}