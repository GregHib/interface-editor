package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.StringProperty

interface GroupOptions {

    var optionCircumfix: StringProperty?
    var optionText: StringProperty?
    var optionAttributes: StringProperty?

    fun setOptionCircumfix(value: String) {
        optionCircumfixProperty().set(value)
    }

    fun getOptionCircumfix(): String {
        return optionCircumfixProperty().get()
    }

    fun optionCircumfixProperty(): StringProperty {
        if (optionCircumfix == null)
            optionCircumfix = StringProperty(this, "optionCircumfix", "")

        return optionCircumfix!!
    }

    fun setOptionText(value: String) {
        optionTextProperty().set(value)
    }

    fun getOptionText(): String {
        return optionTextProperty().get()
    }

    fun optionTextProperty(): StringProperty {
        if (optionText == null)
            optionText = StringProperty(this, "optionText", "")

        return optionText!!
    }

    fun setOptionAttributes(value: String) {
        optionAttributesProperty().set(value)
    }

    fun getOptionAttributes(): String {
        return optionAttributesProperty().get()
    }

    fun optionAttributesProperty(): StringProperty {
        if (optionAttributes == null)
            optionAttributes = StringProperty(this, "optionAttributes", "")

        return optionAttributes!!
    }
}