package com.greg.properties.attributes.types

import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority

class PropertySpacer : Pane {
    constructor() {
        HBox.setHgrow(this, Priority.ALWAYS)
        setMinSize(10.0, 1.0)
    }
}