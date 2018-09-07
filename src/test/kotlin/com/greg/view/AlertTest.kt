package com.greg.view

import javafx.application.Application
import javafx.scene.control.Alert
import tornadofx.*

class AlertTest : View() {

    class CustomAlert : Alert(AlertType.CONFIRMATION) {
        init {
            val fp = vbox {
                label("Enter a number:")
                textfield {
                    filterInput { it.controlNewText.isInt() }
                }
            }
            headerText = ""
            graphic = null

            dialogPane.contentProperty().set(fp)
        }
    }
    /*
    final Button Hyperlink8 = new Button("TextField");
        Hyperlink8.setOnAction(e -> {
            TextInputDialog dlg = new TextInputDialog("");
            dlg.setTitle("Name Check");
            String optionalMasthead = "Please type in your name";
            dlg.getDialogPane().setContentText("What is your name?");
            configureSampleDialog(dlg, optionalMasthead);
            showDialog(dlg);
        });
     */

    override val root = vbox {

        button("Custom").action {
            CustomAlert().showAndWait()
        }
    }

}

class AlertTestApp: App(AlertTest::class)

fun main(args: Array<String>) {
    Application.launch(AlertTestApp::class.java, *args)
}