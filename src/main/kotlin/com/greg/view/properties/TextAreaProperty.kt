package com.greg.view.properties

import javafx.beans.value.ObservableValue
import javafx.scene.control.TextArea
import org.controlsfx.property.editor.AbstractPropertyEditor

class TextAreaProperty(item: PropertyItem) : AbstractPropertyEditor<String, TextArea>(item, TextArea(item.value as String)) {

    override fun setValue(value: String?) {
        editor.text = value
    }

    override fun getObservableValue(): ObservableValue<String> {
        return editor.textProperty()
    }

}