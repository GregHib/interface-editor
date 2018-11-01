package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.StringProperty

interface GroupText {

    var defaultText: StringProperty
    var secondaryText: StringProperty

    fun setDefaultText(value: String) {
        defaultText.set(value)
    }

    fun getDefaultText(): String {
        return defaultText.get()
    }

    fun setSecondaryText(value: String) {
        secondaryText.set(value)
    }

    fun getSecondaryText(): String {
        return secondaryText.get()
    }
}