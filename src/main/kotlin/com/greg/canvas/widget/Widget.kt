package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.properties.Property
import com.greg.properties.PropertyGroup
import com.greg.properties.PropertyRow
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.cast
import kotlin.reflect.full.memberFunctions

class Widget : Group {

    private var components = mutableListOf<WidgetInterface>()

    constructor(vararg component: Node) {

        var rectangle = WidgetRectangle(Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT))
        components.add(rectangle)
        children.add(rectangle)

        for (com in component)
            add(com)
    }


    fun add(vararg node: Node) {
        for (n in node) {
            if (n is WidgetInterface)
                components.add(n)
            children.add(n)
            setWidth(n.layoutBounds.width)
            setHeight(n.layoutBounds.height)
        }
    }

    fun setWidth(width: Double) {
        val component = components.first()
        if (component is WidgetRectangle)
            component.width = width
    }

    fun setHeight(height: Double) {
        val component = components.first()
        if (component is WidgetRectangle)
            component.height = height
    }

    fun setSelection(colour: Color?) {
        val component = components.first()
        if (component is WidgetRectangle)
            component.stroke = colour
    }

    fun getGroups(): List<PropertyGroup>? {
        var list = mutableListOf<PropertyGroup>()
        for (component in components) {
            list.add(createPropertyGroup(component::class.simpleName!!, component))
        }
        return list
    }

    var properties = mutableListOf<Property>()
    lateinit var drag: DragModel

    companion object {
        fun get(name: String, widget: KClass<out WidgetInterface>): KFunction<out WidgetInterface> {
            return widget.memberFunctions.first { it.name == name } as KFunction<out WidgetInterface>
        }
    }

    fun refreshGroups(groups: List<PropertyGroup>, widget: Widget) {
        for(group in groups) {
            for (component in components) {
                if(group.widgetClass != component::class)
                    continue
                refreshGroup(group, component)
            }
        }
    }

    fun refreshGroup(group: PropertyGroup, widget: WidgetInterface) {
        widget.getWidgetProperties()!!
                .filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                .forEachIndexed { index, property ->
                    //Refresh property with the current value
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().refresh(property.thing.cast(property.reflection.call(widget)).value)
                }
    }


    fun link(groups: List<PropertyGroup>) {
        for(group in groups) {
            for (component in components) {
                if(group.widgetClass != component::class)
                    continue
                linkGroup(group, component)
            }
        }
    }

    fun linkGroup(group: PropertyGroup, widget: WidgetInterface) {
        widget.getWidgetProperties()!!
                .filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                .forEachIndexed { index, property ->
                    //Add this widget to the list of outputs for the property row
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().link({ t -> property.thing.cast(property.reflection.call(widget)).value = t })
                }
    }

    fun createPropertyGroup(name: String, widget: WidgetInterface): PropertyGroup {
        //Create a new group
        val group = PropertyRow.createRowGroup(name, widget::class)

//        println(name)
        val properties = widget.getWidgetProperties()
        if (properties != null) {
            for (property in properties) {
//                println(property)
                //To check if property is the same type as group
                if (property.widgetClass != group.widgetClass)
                    continue

                //Force the variable to be accessible to prevent IllegalAccessException if private
//                property.reflection.isAccessible = true
                val value = property.thing.cast(property.reflection.call(widget)).value
                //Handle creation of different types
                when (property.type) {
                    PropertyType.TEXT_FIELD -> group.add(PropertyRow.createTextField(property.title, value.toString()))
                    PropertyType.COLOUR_PICKER -> group.add(PropertyRow.createColourPicker(property.title, value as Color))
                    PropertyType.NUMBER_FIELD -> group.add(PropertyRow.createNumberField(property.title, (value as Double).toInt()))
                }
            }
        }

        return group
    }
}