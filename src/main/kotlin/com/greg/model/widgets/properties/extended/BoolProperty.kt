package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

class BoolProperty(name: String, initialValue: Boolean) : SimpleBooleanProperty(null, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}