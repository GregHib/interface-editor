package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleObjectProperty

class ObjProperty<T: Any>(bean: Any, name: String, initialValue: T) : SimpleObjectProperty<T>(bean, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}