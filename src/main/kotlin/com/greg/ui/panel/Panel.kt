package com.greg.ui.panel

import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.column.Column
import javafx.geometry.Insets
import javafx.scene.layout.AnchorPane
import tornadofx.View
import tornadofx.anchorpane
import tornadofx.stackpane

class Panel(var type: PanelType) : View() {

    lateinit var content: AnchorPane
    override val root = stackpane {
        padding = Insets(0.0)
        content = anchorpane()
    }

    var groups: List<Column>? = null
}