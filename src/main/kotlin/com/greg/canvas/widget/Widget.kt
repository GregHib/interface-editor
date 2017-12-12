package com.greg.canvas.widget

import com.greg.canvas.DragModel
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.AttributeGroup
import com.greg.panels.attributes.parts.AttributeRow
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.scene.Group
import javafx.scene.paint.Color

class Widget : Group, WidgetInterface {

    constructor(builder: WidgetBuilder) {
        components.add(this)

        for (component in builder.components) {
            if (component is WidgetInterface)
                components.add(component)
            children.add(component)
            setWidth(component.layoutBounds.width)
            setHeight(component.layoutBounds.height)
        }
    }

    private var components = mutableListOf<WidgetInterface>()
    var attributes = mutableListOf<Attribute>()
    lateinit var drag: DragModel

    init {
        attributes.add(Attribute("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD, this::class))
        attributes.add(Attribute("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD, this::class))
    }

    override fun getAttributes(type: AttributePaneType): List<Attribute>? {
        return if(type == AttributePaneType.LAYOUT) attributes else null
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
            propertyRow.linkableList.last().refresh(property.getValue(widget))
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