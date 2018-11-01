package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleIntegerProperty

class IntProperty(name: String, initialValue: Int) : SimpleIntegerProperty(null, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}