package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupOptions {

    var optionCircumfix: StringProperty
    var optionText: StringProperty
    var optionAttributes: IntProperty

    fun setOptionCircumfix(value: String) {
        optionCircumfix.set(value)
    }

    fun getOptionCircumfix(): String {
        return optionCircumfix.get()
    }

    fun setOptionText(value: String) {
        optionText.set(value)
    }

    fun getOptionText(): String {
        return optionText.get()
    }

    fun setOptionAttributes(value: Int) {
        optionAttributes.set(value)
    }

    fun getOptionAttributes(): Int {
        return optionAttributes.get()
    }
}