package com.greg.properties

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.HBox

class Attribute : HBox {

    constructor() {
        prefWidth = 280.0
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        alignment = Pos.CENTER
    }
}