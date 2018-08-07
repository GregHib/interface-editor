package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.CacheController
import com.greg.model.widgets.properties.CappedPropertyValues
import com.greg.model.widgets.properties.PanelPropertyValues
import com.greg.model.widgets.type.Widget
import com.greg.view.properties.CappedPropertyItem
import com.greg.view.properties.NumberSpinner
import com.greg.view.properties.PropertyItem
import com.greg.view.properties.TextAreaProperty
import javafx.beans.property.Property
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import javafx.util.Callback
import org.controlsfx.control.PropertySheet
import org.controlsfx.control.PropertySheet.Item
import org.controlsfx.property.editor.Editors
import org.controlsfx.property.editor.PropertyEditor
import tornadofx.Fragment
import tornadofx.drawer
import tornadofx.vbox
import java.util.regex.Pattern


@Suppress("UNCHECKED_CAST")
class RightPane : Fragment() {

    val widgets: WidgetsController by inject()
    val cache: CacheController by inject()
    private val sheet = PropertySheet()
    private val pattern = Pattern.compile("^(-?[0-9]+)")

    init {
        sheet.minWidth = 284.0
        sheet.prefWidth = 284.0
        sheet.propertyEditorFactory = Callback<Item, PropertyEditor<*>> { param ->
            if (param is PropertyItem) {
                when {
                    param.value is Boolean -> Editors.createCheckEditor(param)
                    param.value is Int -> {
                        val editor = NumberSpinner(param)
                        val spinner = editor.editor
                        spinner.isDisable = param.disabled
                        spinner.valueFactory.valueProperty().bindBidirectional(param.propertyValue as Property<Int>?)

                        spinner.editor.textProperty().addListener { _, oldValue, newValue ->
                            if (oldValue != newValue) {
                                val matcher = pattern.matcher(newValue)
                                spinner.editor.text = if (matcher.matches()) matcher.group(1) else oldValue
                            }
                        }

                        spinner.focusedProperty().addListener { _, _, _ ->
                            spinner.editor.fireEvent(ActionEvent())
                        }

                        //Mouse Wheel Scroll to change value
                        spinner.editor.addEventFilter(ScrollEvent.SCROLL) {
                            if (spinner.editor.isFocused) {
                                spinner.valueFactory.increment((it.deltaY / 40).toInt())
                            }
                        }

                        //Can't override setOnAction
                        spinner.addEventHandler(KeyEvent.KEY_PRESSED) {
                            if (spinner.editor.isFocused && it.code == KeyCode.ENTER) {
                                it.consume()
                            }
                        }

                        editor
                    }
                    param.value is Color -> {
                        val editor = Editors.createColorEditor(param)
                        val field = editor.editor
                        (field as? ColorPicker)?.valueProperty()?.bindBidirectional(param.propertyValue as Property<Color>?)
                        editor
                    }
                    else -> {
                        if (param.name == "Archive") {
                            val editor = Editors.createChoiceEditor(param, cache.sprites.getInternalArchiveNames())
                            val field = editor.editor
                            val box = field as? ComboBox<String>
                            box?.valueProperty()?.bindBidirectional(param.propertyValue as Property<String>?)
                            editor
                        } else {
                            val editor = TextAreaProperty(param)//Editors.createTextEditor(param)
                            val field = editor.editor

                            if (field is TextArea) {
                                field.textProperty().bindBidirectional(param.propertyValue as Property<String>?)
                            }
                            editor
                        }
                    }
                }
            } else {
                Editors.createTextEditor(param)
            }

        }
        widgets.getSelection().addListener(ListChangeListener { change ->
            change.next()
            //Get items changed
            val list = if (change.wasAdded()) change.addedSubList else null

            //Clear property sheet
            sheet.items.clear()

            if (list != null) {
                //Add properties for first item only
                val first = list.first()
                first.properties.get()
                        .filter { !(it is PanelPropertyValues && !it.panel) }
                        .forEach { property ->
                            val item = if (property is CappedPropertyValues)
                                CappedPropertyItem(property.category, property.property as Property<*>, property.range)
                            else
                                PropertyItem(property.category, property.property as Property<*>)

                            item.disabled = property.property.isDisabled()

                            sheet.items.add(item)
                        }


                sheet.items
                        .filterIsInstance<PropertyItem>()
                        .forEachIndexed { index, propertyItem ->
                            if (propertyItem.name != "X" && propertyItem.name != "Y")
                                propertyItem.propertyValue.addListener { _, _, newValue ->
                                    change.list
                                            .filter { it != first && it.type == first.type }
                                            .forEach { widget ->
                                                (widget.properties.get().filter {
                                                    !(it is PanelPropertyValues && !it.panel)
                                                }[index].property as Property<*>).value = newValue
                                            }
                                }
                        }
            }
        })
    }

    override val root =
            drawer {
                item("Properties", expanded = true) {
                    vbox {
                        add(sheet)
                    }
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