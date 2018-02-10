package com.greg.view.properties

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import org.controlsfx.control.PropertySheet
import java.util.*

open class PropertyItem(private val propertyName: String, private val propertyCategory: String, private val propertyValue: Property<*>) : PropertySheet.Item {

    val objectProperty = SimpleObjectProperty(this, propertyName, propertyValue.value)//TODO bean is incorrect but prevents leaking
    var disabled: Boolean = false

    init {
        objectProperty.bindBidirectional(propertyValue as Property<Any>?)
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
        return objectProperty.value::class.java
    }

    override fun getValue(): Any {
        return objectProperty.get()
    }

    override fun getObservableValue(): Optional<ObservableValue<out Any>> {
        return Optional.empty()
    }
}