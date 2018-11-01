package com.greg.view.properties

import javafx.beans.value.ObservableValue
import org.controlsfx.property.editor.AbstractPropertyEditor


class IntSpreadsheetProperty(item: RangePropertyItem) : AbstractPropertyEditor<IntArray, IntArraySpreadsheetView>(item, IntArraySpreadsheetView(item)) {

    override fun setValue(value: IntArray) {
        editor.setValueProperty(value)
    }

    override fun getObservableValue(): ObservableValue<IntArray> {
        return editor.getValueProperty()
    }

}