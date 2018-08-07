package com.greg.view.alerts

import tornadofx.filterInput
import tornadofx.isInt

class IntegerAlert(question: String) : CustomAlert(question) {

    var value = 0
        get() = if(textField.text.isEmpty()) 0 else textField.text.toInt()

    init {
        textField.filterInput { it.controlNewText.isInt() }
    }

}