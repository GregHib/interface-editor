package com.greg.view.alerts

class StringAlert(question: String) : CustomAlert(question) {

    var value = ""
        get() = textField.text

}