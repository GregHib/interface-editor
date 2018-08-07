package com.greg.view.properties

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.beans.value.WritableIntegerValue
import org.controlsfx.control.PropertySheet
import tornadofx.property
import java.util.*

open class PropertyItem(private val propertyCategory: String, val propertyValue: Property<*>) : PropertySheet.Item {

    var disabled: Boolean = false

    override fun getName(): String {
        return propertyValue.name.capitalize()
    }

    override fun getCategory(): String {
        return propertyCategory
    }

    override fun setValue(value: Any?) {
        if(propertyValue is WritableIntegerValue)
            propertyValue.set(value as Int)
        else
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