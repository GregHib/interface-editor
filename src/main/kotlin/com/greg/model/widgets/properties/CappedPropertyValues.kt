package com.greg.model.widgets.properties

import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property

class CappedPropertyValues(property: Property<*>, category: String, resize: Boolean, val range: ObjectProperty<IntRange>) : ResizedPropertyValues(property, category, resize)