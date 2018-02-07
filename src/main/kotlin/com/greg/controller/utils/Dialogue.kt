package com.greg.controller.utils

import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import java.awt.Desktop
import java.io.File

object Dialogue {

    fun openDirectory(headerText: String, dir: File) {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Information"
        alert.headerText = headerText
        alert.contentText = "Choose an option."

        val choiceOne = ButtonType("Yes.")
        val close = ButtonType("No", ButtonData.CANCEL_CLOSE)

        alert.buttonTypes.setAll(choiceOne, close)

        val result = alert.showAndWait()

        if (result.isPresent) {

            val type = result.get()

            if (type == choiceOne) {
                try {
                    Desktop.getDesktop().open(dir)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

        }
    }

    fun showInfo(msg: String) : Alert {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Information"
        alert.headerText = "Information"
        alert.contentText = msg
        return alert
    }

    fun showWarning(msg: String) : Alert {
        val alert = Alert(Alert.AlertType.WARNING)
        alert.title = "Warning"
        alert.headerText = "Warning"
        alert.contentText = msg
        return alert
    }

}