package com.greg.model.widgets.properties

import javafx.beans.property.Property

data class PropertyValues(val property: Property<*>, val category: String, val panel: Boolean)