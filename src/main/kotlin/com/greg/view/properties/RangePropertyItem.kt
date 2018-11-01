package com.greg.view.properties

import com.greg.model.widgets.properties.IntValues
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property

class RangePropertyItem(propertyCategory: String, propertyValue: Property<*>, val propertyRange: ObjectProperty<IntValues>) : PropertyItem(propertyCategory, propertyValue)