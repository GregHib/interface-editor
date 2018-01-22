package src.com.greg.model

import javafx.beans.property.Property

class Properties {
    private val properties = mutableListOf<PropertyValues>()

    fun add(property: Property<*>, category: String = "Properties") {
        properties.add(PropertyValues(property, category))
    }

    fun get(): MutableList<PropertyValues> {
        return properties
    }
}