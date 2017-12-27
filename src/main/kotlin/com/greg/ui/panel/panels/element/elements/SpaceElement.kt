package com.greg.ui.panel.panels.element.elements

import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority

class SpaceElement : Pane() {
    init {
        HBox.setHgrow(this, Priority.ALWAYS)
        setMinSize(10.0, 1.0)
    }
}