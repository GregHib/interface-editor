package com.greg.controller.view

import com.greg.ui.panel.panels.element.elements.SpaceElement
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.util.converter.NumberStringConverter
import tornadofx.View
import tornadofx.hbox
import tornadofx.label

@Suppress("UNCHECKED_CAST")
class PanelRow(label: String) : View() {
    override val root = hbox {
        prefWidth = 280.0
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        alignment = Pos.CENTER
        label(label)
    }

    fun create(property: Property<*>) {
        root.add(SpaceElement())
        if (property is IntegerProperty) {
            root.add(NumberElement(property.get()))
        } else if (property is StringProperty) {
            root.add(TextElement(property.get()))
        } else if (property is ObjectProperty && property.get() is Color) {
            root.add(ColourElement(property.get() as Color))
        }
    }

    fun unlink(property: Property<*>) {
        unlinkBidirectional(property)
    }

    fun link(property: Property<*>, primary: Boolean) {
        if(primary)
            linkBidirectional(property)
        else
            link(property)
    }

    /**
     *  Adds a one way property listener to change
     *  the non-primary selected widgets only when
     *  modified via panel
     *  (Bidirectional would mimic primary)
     */
    private fun link(property: Property<*>) {
        val element = root.children.last()//TODO multi-support
        if (property is IntegerProperty && element is NumberElement) {
            element.textProperty().addListener({ _, _, newValue -> property.value = newValue.toInt() })
        } else if (property is StringProperty && element is TextElement) {
            element.textProperty().addListener({ _, _, newValue -> property.value = newValue })
        } else if (property is ObjectProperty && property.get() is Color && element is ColourElement) {
            element.valueProperty().addListener({ _, _, newValue -> property.value = newValue })
        }
    }

    /**
     * Adds bidirectional property bind to
     * the primary selected widget,
     * if changed via panel or canvas value stays same
     */
    private fun linkBidirectional(property: Property<*>) {
        val element = root.children.last()//TODO multi-support
        if (property is IntegerProperty && element is NumberElement) {
            element.textProperty().bindBidirectional(property, NumberStringConverter())

            //Listener to fix capped values
            element.textProperty().addListener({ _, _, newValue ->
                if (newValue != property.get().toString())
                    element.text = property.get().toString()
            })
        } else if (property is StringProperty && element is TextElement) {
            element.textProperty().bindBidirectional(property)
        } else if (property is ObjectProperty && property.get() is Color && element is ColourElement) {
            element.valueProperty().bindBidirectional(property as Property<Color>)
        }
    }

    private fun unlinkBidirectional(property: Property<*>) {
        val element = root.children.last()//TODO multi-support
        if (element is NumberElement) {
            element.textProperty().unbindBidirectional(property)
        } else if (element is TextElement) {
            element.textProperty().unbindBidirectional(property)
        } else if (property is ObjectProperty && property.get() is Color && element is ColourElement) {
            element.valueProperty().unbindBidirectional(property as Property<Color>)
        }
    }
}