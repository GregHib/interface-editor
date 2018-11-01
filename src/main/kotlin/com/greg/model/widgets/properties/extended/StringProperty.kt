package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleStringProperty

class StringProperty(name: String, initialValue: String) : SimpleStringProperty(null, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}