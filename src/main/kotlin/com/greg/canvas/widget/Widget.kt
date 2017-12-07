package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.properties.Property
import com.greg.properties.PropertyGroup
import com.greg.properties.PropertyRow
import com.greg.properties.PropertyType
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
    abstract fun getGroups(): List<PropertyGroup>
    abstract fun linkGroups(groups: MutableList<PropertyGroup>)
    abstract fun refreshGroups(groups: MutableList<PropertyGroup>)

    companion object {
        fun get(name: String, widget: KClass<out Widget>): KMutableProperty1<out Widget, *> {
            return widget.memberProperties.first { it.name == name } as KMutableProperty1<out Widget, *>
        }
    }

    fun refreshGroup(group: PropertyGroup, widget: Widget) {
        properties
                .filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                .forEachIndexed { index, property ->
                    //Refresh property with the current value
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().refresh(property.reflection.getter.call(widget))
                }
    }

    fun linkGroup(group: PropertyGroup, widget: Widget) {
        properties
                .filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                .forEachIndexed { index, property ->
                    //Add this widget to the list of outputs for the property row
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().link({ t -> property.reflection.setter.call(widget, t) })
                }
    }

    abstract fun handleReflection(property: Property): Any?

    fun createPropertyGroup(name:String, widget: KClass<out Widget>): PropertyGroup {
        //Create a new group
        val group = PropertyRow.createRowGroup(name, widget)

        for (property in properties) {
            //To check if property is the same type as group
            if(property.widgetClass != group.widgetClass)
                continue

            //Force the variable to be accessible to prevent IllegalAccessException if private
            property.reflection.isAccessible = true

            //Handle creation of different types
            when(property.type) {
                PropertyType.TEXT_FIELD -> group.add(PropertyRow.createTextField(property.title, handleReflection(property).toString()))
                PropertyType.COLOUR_PICKER -> group.add(PropertyRow.createColourPicker(property.title, handleReflection(property) as Color))
                PropertyType.NUMBER_FIELD -> {
                    println(handleReflection(property) as Double)
                    group.add(PropertyRow.createNumberField(property.title, (handleReflection(property) as Double).toInt()))
                }
            }
        }

        return group
    }
}