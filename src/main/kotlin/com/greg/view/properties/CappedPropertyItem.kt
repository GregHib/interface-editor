package com.greg.view.properties

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property

class CappedPropertyItem(propertyName: String, propertyCategory: String, propertyValue: Property<*>, val propertyRange: ObjectProperty<IntRange>) : PropertyItem(propertyName, propertyCategory, propertyValue) {

}