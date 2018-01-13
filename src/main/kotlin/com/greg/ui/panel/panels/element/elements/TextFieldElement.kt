package com.greg.ui.panel.panels.element.elements

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.panel.panels.element.Element
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class TextFieldElement(private var default: String?) : TextField(), Element {

    override var links: MutableList<(value: Any?) -> Unit> = mutableListOf()
    private var beginText = default

    init {
        text = beginText

        // Key press
        // ---------------------------------------------------------------
        addEventFilter<KeyEvent>(KeyEvent.ANY, { e -> handleKeyEvent(e)})

        // Focus listener
        // ---------------------------------------------------------------
        focusedProperty().addListener({ _, _, focus -> handleFocusListener(focus) })
    }

    private fun handleKeyEvent(e: KeyEvent) {
        when(e.eventType) {
            KeyEvent.KEY_PRESSED -> handleKeyPress(e.code)
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
            if(Settings.getBoolean(SettingsKey.CANCEL_ON_UNFOCUSED))
                cancel()
            else
                accept(text)
        } else
            beginText = text
    }

    override fun refresh(value: Any?) {
        this.text = value.toString()
    }

    private fun accept(text: String?) {
        for(action in links) {
            action(text)
        }
        beginText = text
    }

    private fun cancel() {
        text = beginText
    }

    private fun resetToDefault() {
        text = default
    }
}