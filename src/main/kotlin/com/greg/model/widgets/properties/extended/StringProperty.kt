package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleStringProperty

class StringProperty(bean: Any, name: String, initialValue: String) : SimpleStringProperty(bean, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}