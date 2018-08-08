package com.greg.model.widgets.type.groups

import com.greg.model.settings.Settings
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupText {

    var defaultText: StringProperty?
    var secondaryText: StringProperty?

    fun setDefaultText(value: String) {
        defaultTextProperty().set(value)
    }

    fun getDefaultText(): String {
        return defaultTextProperty().get()
    }

    fun defaultTextProperty(): StringProperty {
        if (defaultText == null)
            defaultText = StringProperty(this, "defaultText", Settings.get(Settings.DEFAULT_TEXT_MESSAGE))

        return defaultText!!
    }

    fun setSecondaryText(value: String) {
        secondaryTextProperty().set(value)
    }

    fun getSecondaryText(): String {
        return secondaryTextProperty().get()
    }

    fun secondaryTextProperty(): StringProperty {
        if (secondaryText == null)
            secondaryText = StringProperty(this, "secondaryText", Settings.get(Settings.DEFAULT_TEXT_SECONDARY_MESSAGE))

        return secondaryText!!
    }
}