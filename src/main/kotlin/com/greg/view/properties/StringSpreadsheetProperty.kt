package com.greg.view.properties

import javafx.beans.value.ObservableValue
import org.controlsfx.property.editor.AbstractPropertyEditor


class StringSpreadsheetProperty(item: PropertyItem) : AbstractPropertyEditor<Array<String>, StringArraySpreadsheetView>(item, StringArraySpreadsheetView(item)) {

    override fun setValue(value: Array<String>) {
        editor.setValueProperty(value)
    }

    override fun getObservableValue(): ObservableValue<Array<String>> {
        return editor.getValueProperty()
    }

}