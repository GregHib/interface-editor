package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

interface ToggleProperty {
    var disabled: BooleanProperty?

    fun isDisabled(): Boolean {
        return disabledProperty().get()
    }

    fun setDisabled(value: Boolean) {
        disabledProperty().set(value)
    }

    fun disabledProperty(): BooleanProperty {
        if (disabled == null)
            disabled = SimpleBooleanProperty( null, "disabled", false)

        return disabled!!
    }
}