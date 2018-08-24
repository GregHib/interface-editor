package com.greg.model.widgets.properties.extended

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleObjectProperty

class ObjProperty<T: Any>(name: String, initialValue: T) : SimpleObjectProperty<T>(null, name, initialValue), ToggleProperty {
    override var disabled: BooleanProperty? = null
}