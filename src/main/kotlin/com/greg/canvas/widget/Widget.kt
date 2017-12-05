package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.properties.PropertyType
import com.greg.properties.attributes.Property
import com.greg.properties.attributes.PropertyGroup
import com.greg.properties.attributes.PropertyRow
import com.greg.properties.attributes.types.ColourPickerProperty
import com.greg.properties.attributes.types.TextFieldProperty
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class Widget: Group() {

    var properties = mutableListOf<Property>()
    lateinit var drag: DragModel
    abstract fun setSelection(colour: Paint?)
    abstract fun getGroup(): List<PropertyGroup>
    abstract fun handleGroup(groups: MutableList<PropertyGroup>)


    companion object {
        fun get(name: String, widget: KClass<out Widget>): KMutableProperty1<out Widget, *> {
            return widget.memberProperties.first { it.name == name } as KMutableProperty1<out Widget, *>
        }
    }

    fun linkGroup(group: PropertyGroup, widget: Widget) {
        for ((index, property) in properties.withIndex()) {
            property.reflection.setter.isAccessible = true
            when(property.type) {
                PropertyType.TEXT_FIELD -> (group.properties[index].children.last() as TextFieldProperty).link({ t -> property.reflection.setter.call(widget, t) })
                PropertyType.COLOUR_PICKER -> (group.properties[index].children.last() as ColourPickerProperty).link({ t -> property.reflection.setter.call(widget, t) })
            }
        }
    }

    abstract fun handleReflection(property: Property): Any?

    fun createPropertyGroup(name:String): PropertyGroup {
        val group = PropertyRow.createRowGroup(name)

        for (property in properties) {
            property.reflection.isAccessible = true
            when(property.type) {
                PropertyType.TEXT_FIELD -> group.add(PropertyRow.createTextField(property.title, handleReflection(property).toString()))
                PropertyType.COLOUR_PICKER -> group.add(PropertyRow.createColourPicker(property.title, handleReflection(property) as Color))
            }
        }

        return group
    }
}