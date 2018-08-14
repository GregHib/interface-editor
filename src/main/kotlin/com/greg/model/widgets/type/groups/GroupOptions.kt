package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.StringProperty

interface GroupOptions {

    var optionCircumfix: StringProperty?
    var optionText: StringProperty?
    var optionAttributes: IntProperty?

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

    fun setOptionAttributes(value: Int) {
        optionAttributesProperty().set(value)
    }

    fun getOptionAttributes(): Int {
        return optionAttributesProperty().get()
    }

    fun optionAttributesProperty(): IntProperty {
        if (optionAttributes == null)
            optionAttributes = IntProperty(this, "optionAttributes", 0)

        return optionAttributes!!
    }
}