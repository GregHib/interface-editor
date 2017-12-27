package com.greg.ui.canvas.state.states.edit.resize.observer

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

class Listener(private val observer: WidgetObserver) : ChangeListener<Number> {

    override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
        observer.onChange()
    }
}