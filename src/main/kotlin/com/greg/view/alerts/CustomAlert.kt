package com.greg.view.alerts

import javafx.scene.control.Alert
import tornadofx.add
import tornadofx.label
import tornadofx.textfield
import tornadofx.vbox

abstract class CustomAlert(question: String) : Alert(Alert.AlertType.CONFIRMATION, question) {

    internal val textField = textfield()

    init {
        val box = vbox {
            label(question)
            add(textField)
        }
        headerText = ""
        graphic = null
        title = "Select"

        dialogPane.contentProperty().set(box)
    }
}
