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
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class Widget(vararg component: Node) : Group(), WidgetInterface {

    private var components = mutableListOf<WidgetInterface>()
    var attributes = mutableListOf<Attribute>()
    lateinit var drag: DragModel

    init {
        attributes.add(Attribute("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD, this::class))
        attributes.add(Attribute("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD, this::class))
    }

    init {
        components.add(this)
        val rectangle = WidgetRectangle(Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT))
        components.add(rectangle)
        children.add(rectangle)
        for (com in component)
            add(com)
    }

    companion object {
        fun get(name: String, widget: KClass<WidgetInterface>, function: Boolean): KCallable<*> {
            return if(function)
                widget.memberFunctions.first { it.name == name }
            else
                widget.memberProperties.first { it.name == name }
        }
    }

    override fun getAttributes(type: AttributePaneType): List<Attribute>? {
        return if(type == AttributePaneType.LAYOUT) attributes else null
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

    private fun setWidth(width: Double) {
        val component = components[1]
        if (component is WidgetRectangle)
            component.width = width
    }

    private fun setHeight(height: Double) {
        val component = components[1]
        if (component is WidgetRectangle)
            component.height = height
    }

    fun setSelection(colour: Color?) {
        val component = components[1]
        if (component is WidgetRectangle)
            component.stroke = colour
    }


    /**
     * Attribute refreshing
     */

    fun refresh(groups: List<AttributeGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { refreshGroup(group, it, type) }
        }
    }

    private fun refreshGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)
                ?.filter { it.widgetClass == group.widgetClass }
                ?.forEachIndexed { index, property ->
            //Refresh property with the current value
            val propertyRow = group.properties[index]
            propertyRow.linkableList.last().refresh((property.reflection.call(widget) as WritableValue<*>).value)
        }
    }


    /**
     * Attribute linking
     */

    fun link(groups: List<AttributeGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { group.widgetClass == it::class }
                    .forEach { linkGroup(group, it, type) }
        }
    }

    private fun linkGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)
                ?.filter { it.widgetClass == group.widgetClass }//If the property is same type as group
                ?.forEachIndexed { index, property ->
                    //Add this widget to the list of outputs for the property row
                    val propertyRow = group.properties[index]
                    propertyRow.linkableList.last().link({ t -> property.setValue(widget, t) })
                }
    }

    /**
     * Group creation
     */

    fun getGroups(type: AttributePaneType): List<AttributeGroup>? {
        val list = mutableListOf<AttributeGroup>()
        for (component in components) {

            val attributes = component.getAttributes(type)
            if (attributes != null)
                list.add(createGroup(component::class.simpleName!!, component, attributes))
        }
        return list
    }

    private fun createGroup(name: String, widget: WidgetInterface, attributes: List<Attribute>?): AttributeGroup {
        //Create a new group
        val group = AttributeGroup(name, widget::class)

        if (attributes != null) {
            for (attribute in attributes) {
                //To check if attribute is the same type as group
                if (attribute.widgetClass != group.widgetClass)
                    continue

                //Get the attribute's current value via reflection
                val value = attribute.getValue(widget)

                //Handle creation of different types
                when (attribute.type) {
                    AttributeType.TEXT_FIELD -> group.add(AttributeRow.createTextField(attribute.title, value.toString()))
                    AttributeType.COLOUR_PICKER -> group.add(AttributeRow.createColourPicker(attribute.title, value as Color))
                    AttributeType.NUMBER_FIELD -> group.add(AttributeRow.createNumberField(attribute.title, (value as Double).toInt()))
                }
            }
        }

        return group
    }
}