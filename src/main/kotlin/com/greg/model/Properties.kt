package com.greg.model

import javafx.beans.property.Property

class Properties {
    private val properties = mutableListOf<PropertyValues>()

    fun add(property: Property<*>, category: String = "Properties") {
        properties.add(PropertyValues(property, category, true))
    }

    fun add(property: Property<*>, panel: Boolean = true, category: String = "Properties") {
        properties.add(PropertyValues(property, category, panel))
    }

    fun get(): MutableList<PropertyValues> {
        return properties
    }
}