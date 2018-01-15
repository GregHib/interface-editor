package com.greg.controller.view

import javafx.beans.property.SimpleIntegerProperty

class LimitedIntegerProperty(bean: Any?, name: String?, initialValue: Int, private val limit: (value: Int) -> Int) : SimpleIntegerProperty(bean, name, initialValue) {

    override fun set(newValue: Int) {
        super.set(limit(newValue))
    }

}