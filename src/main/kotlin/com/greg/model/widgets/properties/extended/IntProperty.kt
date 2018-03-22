package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleIntegerProperty

class IntProperty(bean: Any, name: String, initialValue: Int) : SimpleIntegerProperty(bean, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}