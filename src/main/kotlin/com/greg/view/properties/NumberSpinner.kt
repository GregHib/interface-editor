package com.greg.view.properties

import com.greg.model.widgets.properties.IntValues
import javafx.beans.value.ObservableValue
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import org.controlsfx.property.editor.AbstractPropertyEditor

class NumberSpinner(item: PropertyItem) : AbstractPropertyEditor<Int, Spinner<Int>>(item, Spinner(Int.MIN_VALUE, Int.MAX_VALUE, item.value as Int)) {

    init {
        if(item is RangePropertyItem) {
            reloadCaps(item.propertyRange.get())
            item.propertyRange.addListener { _, _, newValue ->
                reloadCaps(newValue)
            }
        }
    }

    private fun reloadCaps(range: IntValues) {
        editor.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(range.first, range.last, editor.value)
    }
    override fun setValue(value: Int?) {
        editor.editor.text = value.toString()
    }

    override fun getObservableValue(): ObservableValue<Int> {
        return editor.valueProperty()
    }

}