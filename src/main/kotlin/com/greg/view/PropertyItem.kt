package com.greg.view

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import org.controlsfx.control.PropertySheet
import java.util.*

class PropertyItem : PropertySheet.Item {
    private val propertyName: String
    private val propertyCategory: String
    private val propertyValue: Property<*>

    constructor(propertyName: String, propertyCategory: String, propertyValue: Property<*>) {
        this.propertyName = propertyName
        this.propertyCategory = propertyCategory
        this.propertyValue = propertyValue
    }

    override fun getName(): String {
        return propertyName
    }

    override fun getCategory(): String {
        return propertyCategory
    }

    override fun setValue(value: Any?) {
        propertyValue.value = value
    }

    override fun getDescription(): String? {
        return null
    }

    override fun getType(): Class<*> {
        return propertyValue.value::class.java
    }

    override fun getValue(): Any {
        return propertyValue.value
    }

    override fun getObservableValue(): Optional<ObservableValue<out Any>> {
        return Optional.empty()
    }
}