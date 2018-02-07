package com.greg.model.widgets.properties

import javafx.beans.property.SimpleIntegerProperty

class CustomIntegerProperty(bean: Any?, name: String?, initialValue: Int, private val limit: (value: Int, newValue: Int) -> Int) : SimpleIntegerProperty(bean, name, initialValue) {

    override fun set(newValue: Int) {
        super.set(limit(value, newValue))
    }

}