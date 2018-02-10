package com.greg.model.widgets.properties

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property

class Properties {
    private val properties = mutableListOf<PropertyValues>()

    fun get(): MutableList<PropertyValues> {
        return properties
    }

    fun get(property: Property<*>): PropertyValues? {
        return properties.firstOrNull { it.property == property }
    }

    fun add(property: Property<*>, category: String = "Properties") {
        properties.add(PropertyValues(property, category))
    }

    fun addPanel(property: Property<*>, panel: Boolean, category: String = "Properties") {
        properties.add(PanelPropertyValues(property, category, panel))
    }

    fun addEditable(property: Property<*>, resize: Boolean, category: String = "Properties") {
        properties.add(ResizedPropertyValues(property, category, resize))
    }

    fun addCapped(property: Property<*>, range: ObjectProperty<IntRange>, resize: Boolean = false, category: String = "Properties") {
        properties.add(CappedPropertyValues(property, category, resize, range))
    }
}