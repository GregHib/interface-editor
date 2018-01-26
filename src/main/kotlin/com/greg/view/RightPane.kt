package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.Widget
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.collections.ListChangeListener
import javafx.scene.control.ColorPicker
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.util.Callback
import javafx.util.converter.IntegerStringConverter
import org.controlsfx.control.PropertySheet
import org.controlsfx.control.PropertySheet.Item
import org.controlsfx.property.editor.Editors
import org.controlsfx.property.editor.PropertyEditor
import tornadofx.Fragment
import tornadofx.drawer
import tornadofx.vbox


@Suppress("UNCHECKED_CAST")
class RightPane : Fragment() {

    val widgets: WidgetsController by inject()
    private val sheet = PropertySheet()

    init {
        sheet.minWidth = 284.0
        sheet.propertyEditorFactory = Callback<Item, PropertyEditor<*>> { param ->
            if (param is PropertyItem) {
                when {
                    param.value is Boolean -> Editors.createCheckEditor(param)
                    param.value is Int -> {
                        val editor = Editors.createNumericEditor(param)
                        val field = editor.editor

                        if (field is TextField) {
                            Bindings.bindBidirectional(field.textProperty(), param.objectProperty as Property<Int>?, IntegerStringConverter())
                            field.focusedProperty().addListener { _, _, newValue ->
                                if(newValue)
                                    widgets.start()
                                else
                                    widgets.finish()
                            }
                            field.setOnAction {
                                widgets.finish()
                            }
                        }

                        editor
                    }
                    param.value is Color -> {
                        val editor = Editors.createColorEditor(param)
                        val field = editor.editor

                        if(field is ColorPicker)
                            field.valueProperty().bindBidirectional(param.objectProperty as Property<Color>?)
                        editor
                    }
                    else -> {
                        val editor = Editors.createTextEditor(param)
                        val field = editor.editor

                        if(field is TextField) {
                            field.textProperty().bindBidirectional(param.objectProperty as Property<String>?)
                            field.focusedProperty().addListener { _, _, newValue ->
                                if(newValue)
                                    widgets.start()
                                else
                                    widgets.finish()
                            }
                            field.setOnAction {
                                widgets.finish()
                            }
                        }
                        editor
                    }
                }
            } else {
                Editors.createTextEditor(param)
            }

        }
        widgets.getSelection().addListener(ListChangeListener {
            it.next()
            //Get items changed
            val list = if (it.wasAdded()) it.addedSubList else null
            //Clear property sheet
            sheet.items.clear()

            if (list != null) {
                //Add properties for first item only
                val first = list.first()
                first.properties.get()
                        .filter { it.panel }
                        .forEach { property -> sheet.items.add(PropertyItem(property.property.name.capitalize(), property.category, property.property)) }

                sheet.items
                        .filterIsInstance<PropertyItem>()
                        .forEachIndexed { index, propertyItem ->
                            if (propertyItem.objectProperty.name != "X" && propertyItem.objectProperty.name != "Y")
                                propertyItem.objectProperty.addListener { _, _, newValue ->
                                    it.list
                                            .filter { it != first && it.type == first.type }
                                            .forEach { widget ->
                                                widget.properties.get().filter { it.panel }[index].property.value = newValue
                                            }
                                }
                        }
            }
        })
    }

    override val root = drawer {
        item("Properties", expanded = true) {
            vbox {
                add(sheet)
            }
        }
        item("CS2 Editor") {
        }
    }

    val onSelection: (String) -> Unit = {
        sheet.items.clear()
    }

    fun refresh(it: ListChangeListener.Change<out Widget>) {
        if (it.wasAdded())
            println(it)
    }
}