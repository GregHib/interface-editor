package com.greg.properties

import com.greg.canvas.widget.WidgetText
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class AttributeTextField : TextField {

    private var widget: WidgetText

    constructor(value: WidgetText) {
        this.widget = value
        text = value.getText()

        // Key press
        // ---------------------------------------------------------------
        addEventFilter<KeyEvent>(KeyEvent.ANY, { e -> handleKeyEvent(e)})


        // Focus listener
        // ---------------------------------------------------------------
        focusedProperty().addListener({ _, _, focus -> handleFocusListener(focus) })
    }

    private fun handleKeyEvent(e: KeyEvent) {
        when(e.eventType) {
            KeyEvent.KEY_PRESSED -> { handleKeyPress(e.code) }
        }
    }

    private fun handleKeyPress(code: KeyCode) {
        when(code) {
            KeyCode.ENTER -> { accept() }
            KeyCode.ESCAPE -> { cancel() }
        }
    }

    private fun handleFocusListener(focused: Boolean) {
        if(focused) {

        } else {
            accept()
        }
    }

    private fun accept() {
        widget.setText(text)
    }

    private fun cancel() {
        text = widget.getText()
        this.isFocusTraversable = false
    }
}