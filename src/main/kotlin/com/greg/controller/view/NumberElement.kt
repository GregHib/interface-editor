package com.greg.controller.view

import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.util.converter.IntegerStringConverter

class NumberElement(val default: Int) : TextField() {

    init {
        textFormatter = TextFormatter(IntegerStringConverter())
    }
}