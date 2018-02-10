package com.greg.model.widgets.properties

import javafx.beans.property.Property

open class ResizedPropertyValues(property: Property<*>, category: String, val resize: Boolean) : PropertyValues(property, category)