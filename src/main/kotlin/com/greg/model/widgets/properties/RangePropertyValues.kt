package com.greg.model.widgets.properties

import com.greg.model.widgets.properties.extended.ToggleProperty
import javafx.beans.property.ObjectProperty

class RangePropertyValues(property: ToggleProperty, category: String, val range: ObjectProperty<IntValues>) : PropertyValues(property, category)