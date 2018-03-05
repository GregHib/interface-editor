package com.greg.model.widgets.properties

import com.greg.model.widgets.properties.extended.ToggleProperty
import javafx.beans.property.ObjectProperty

class CappedPropertyValues(property: ToggleProperty, category: String, val range: ObjectProperty<IntRange>) : PropertyValues(property, category)