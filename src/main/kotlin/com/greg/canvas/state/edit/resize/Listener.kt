package com.greg.canvas.state.edit.resize

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

class Listener(private val changeInterface: WidgetChangeInterface) : ChangeListener<Number> {

    override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
        changeInterface.onChange()
    }
}