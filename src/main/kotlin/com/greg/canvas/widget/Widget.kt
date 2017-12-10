package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.panels.attributes.AttributePaneType
import com.greg.properties.Property
import com.greg.properties.PropertyGroup
import com.greg.properties.PropertyRow
import com.greg.properties.PropertyType
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.beans.value.WritableValue
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions

class Widget : Group, WidgetInterface {

    private var components = mutableListOf<WidgetInterface>()

    constructor(vararg component: Node) {
        components.add(this)

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

    fun getGroups(type: AttributePaneType): List<PropertyGroup>? {
        val list = mutableListOf<PropertyGroup>()
        for (component in components) {

            val properties = component.getProperties(type)
            if (properties != null)
                list.add(createPropertyGroup(component::class.simpleName!!, component, properties))
        }

        return list
    }


    var attributes = mutableListOf<Property>()

    init {
        attributes.add(Property("Location X", "layoutXProperty", PropertyType.NUMBER_FIELD, this::class))
        attributes.add(Property("Location Y", "layoutYProperty", PropertyType.NUMBER_FIELD, this::class))
    }

    override fun getProperties(type: AttributePaneType): List<Property>? {
        when (type) {
            AttributePaneType.LAYOUT -> return attributes
        }
        return null
    }

    lateinit var drag: DragModel

    companion object {
        fun get(name: String, widget: KClass<out WidgetInterface>): KFunction<out WidgetInterface> {
            return widget.memberFunctions.first { it.name == name } as KFunction<out WidgetInterface>
        }
    }

    /*
    TODO there are defiantly too many loops here

    TODO make widget linkable? As layoutX/Y are the position of the widget
    TODO add Layout titled tab and have it separate (but loaded using same methods as Properties)

    TODO is rectangle needed by default?
     */
    fun refreshGroups(groups: List<PropertyGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { refreshGroup(group, it, type) }
        }
    }

    private fun refreshGroup(group: PropertyGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getProperties(type)
                ?.filter { it.widgetClass == group.widgetClass }
                ?.forEachIndexed { index, property ->
            //Refresh property with the current value
            val propertyRow = group.properties[index]
            propertyRow.linkableList.last().refresh((property.reflection.call(widget) as WritableValue<*>).value)
        }
    }


    fun link(groups: List<PropertyGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { linkGroup(group, it, type) }
        }
    }

    private fun linkGroup(group: PropertyGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getProperties(type)
                ?.filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                ?.forEachIndexed { index, property ->
                    //Add this widget to the list of outputs for the property row
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().link({ t -> (property.reflection.call(widget) as WritableValue<*>).value = t })
                }
    }

    fun createPropertyGroup(name: String, widget: WidgetInterface, properties: List<Property>?): PropertyGroup {
        //Create a new group
        val group = PropertyRow.createRowGroup(name, widget::class)

        if (properties != null) {
            for (property in properties) {
                //To check if property is the same type as group
                if (property.widgetClass != group.widgetClass)
                    continue

                val value = (property.reflection.call(widget) as WritableValue<*>).value
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