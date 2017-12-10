package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.AttributeGroup
import com.greg.panels.attributes.parts.AttributeRow
import com.greg.panels.attributes.parts.pane.AttributePaneType
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
        val component = components[1]
        if (component is WidgetRectangle)
            component.width = width
    }

    fun setHeight(height: Double) {
        val component = components[1]
        if (component is WidgetRectangle)
            component.height = height
    }

    fun setSelection(colour: Color?) {
        val component = components[1]
        if (component is WidgetRectangle)
            component.stroke = colour
    }

    fun getGroups(type: AttributePaneType): List<AttributeGroup>? {
        val list = mutableListOf<AttributeGroup>()
        for (component in components) {

            val properties = component.getProperties(type)
            if (properties != null)
                list.add(createGroup(component::class.simpleName!!, component, properties))
        }

        return list
    }


    var attributes = mutableListOf<Attribute>()

    init {
        attributes.add(Attribute("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD, this::class))
        attributes.add(Attribute("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD, this::class))
    }

    override fun getProperties(type: AttributePaneType): List<Attribute>? {
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

    fun refreshGroups(groups: List<AttributeGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { refreshGroup(group, it, type) }
        }
    }

    private fun refreshGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getProperties(type)
                ?.filter { it.widgetClass == group.widgetClass }
                ?.forEachIndexed { index, property ->
            //Refresh property with the current value
            val propertyRow = group.properties[index]
            propertyRow.linkableList.last().refresh((property.reflection.call(widget) as WritableValue<*>).value)
        }
    }


    fun link(groups: List<AttributeGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { linkGroup(group, it, type) }
        }
    }

    private fun linkGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getProperties(type)
                ?.filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                ?.forEachIndexed { index, property ->
                    //Add this widget to the list of outputs for the property row
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().link({ t -> (property.reflection.call(widget) as WritableValue<*>).value = t })
                }
    }

    private fun createGroup(name: String, widget: WidgetInterface, attributes: List<Attribute>?): AttributeGroup {
        //Create a new group
        val group = AttributeGroup(name, widget::class)

        if (attributes != null) {
            for (property in attributes) {
                //To check if property is the same type as group
                if (property.widgetClass != group.widgetClass)
                    continue

                val value = (property.reflection.call(widget) as WritableValue<*>).value
                //Handle creation of different types
                when (property.type) {
                    AttributeType.TEXT_FIELD -> group.add(AttributeRow.createTextField(property.title, value.toString()))
                    AttributeType.COLOUR_PICKER -> group.add(AttributeRow.createColourPicker(property.title, value as Color))
                    AttributeType.NUMBER_FIELD -> group.add(AttributeRow.createNumberField(property.title, (value as Double).toInt()))
                }
            }
        }

        return group
    }
}