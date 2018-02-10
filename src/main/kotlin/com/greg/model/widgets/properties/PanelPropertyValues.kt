package com.greg.model.widgets.properties

import javafx.beans.property.Property

class PanelPropertyValues(property: Property<*>, category: String, val panel: Boolean) : PropertyValues(property, category)