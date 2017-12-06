package com.greg.properties.attributes.types

import com.greg.properties.attributes.Linkable
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class TextFieldProperty : TextField, Linkable {

    override var links: MutableList<(value: Any?) -> Unit> = mutableListOf()
    private var default: String?

    constructor(default: String?) {
        this.default = default
        this.text = default

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
        when(code.ordinal) {
            Settings.getInt(SettingsKey.ACCEPT_KEY_CODE) -> { accept(text) }
            Settings.getInt(SettingsKey.CANCEL_KEY_CODE) -> { cancel() }
        }
    }

    private fun handleFocusListener(focused: Boolean) {
        if(!focused) {
            if(Settings.getBoolean(SettingsKey.CANCEL_ON_DEFOCUS))
                cancel()
            else
                accept(text)
        }
    }

    private fun accept(text: String?) {
        for(action in links) {
            action(text)
        }
    }

    private fun cancel() {
        text = default
    }

    override fun link(action: (value: Any?) -> Unit) {
        if(action !is (value: String?) -> Unit)
            throw UnsupportedOperationException()
        this.links.add(action)
    }
}