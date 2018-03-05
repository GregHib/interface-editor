package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty

class BoolProperty(bean: Any, name: String, initialValue: Boolean) : SimpleBooleanProperty(bean, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}