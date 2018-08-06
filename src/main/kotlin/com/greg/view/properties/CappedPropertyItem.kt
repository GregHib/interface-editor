package com.greg.view.properties

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property

class CappedPropertyItem(propertyCategory: String, propertyValue: Property<*>, val propertyRange: ObjectProperty<IntRange>) : PropertyItem(propertyCategory, propertyValue) {

}