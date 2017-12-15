package com.greg.canvas.widget

import com.greg.canvas.WidgetCanvas
import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.AttributeGroup
import com.greg.panels.attributes.parts.AttributeRowBuilder
import com.greg.panels.attributes.parts.pane.AttributePane
import com.greg.panels.attributes.parts.pane.AttributePaneType



class Widget(builder: WidgetBuilder) : WidgetData(builder) {

    init {
        addToStart(this)
        attributes.addLayout("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD)
        attributes.addLayout("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD)
    }

    fun init(canvas: WidgetCanvas): Widget {
        layoutXProperty().addListener { _, _, _ -> canvas.refreshPosition() }
        layoutYProperty().addListener { _, _, _ -> canvas.refreshPosition() }
        return this
    }

    /**
     * Attribute refreshing
     */

    fun refresh(groups: List<AttributeGroup>, type: AttributePaneType) {
        for (group in groups) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget -> refreshGroup(group, widget, type) }
        }
    }

    private fun refreshGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)?.forEachIndexed { index, property ->
            //Refresh property with the current value
            val propertyRow = group.rows[index]
            propertyRow.linkableList.last().refresh(property.getValue(widget))
        }
    }


    /**
     * Attribute linking
     */

    fun link(pane: AttributePane) {
        for (group in pane.groups!!) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget -> linkGroup(group, widget, pane.type) }
        }
    }

    private fun linkGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)?.forEachIndexed { index, property ->
            //Add this widget to the list of outputs for the property row
            val propertyRow = group.rows[index]
            propertyRow.linkableList.last().link({ value -> property.setValue(widget, value) })
        }
    }

    /**
     * Group creation
     */
    fun getGroups(type: AttributePaneType): List<AttributeGroup>? {
        val list = mutableListOf<AttributeGroup>()
        for (component in components) {
            val attributes = component.getAttributes(type)
            if (attributes != null && attributes.isNotEmpty())
                list.add(createGroup(component::class.simpleName!!, component, attributes))
        }
        return list
    }

    private fun createGroup(name: String, widget: WidgetInterface, attributes: List<Attribute>): AttributeGroup {
        //Create a new group
        val group = AttributeGroup(name, widget::class)

        for (attribute in attributes) {
            //Get the attribute's current value via reflection
            val value = attribute.getValue(widget)

            //Create and add row
            val builder = AttributeRowBuilder(attribute.title)
            builder.addAttribute(attribute.type, value)
            group.add(builder.build())
        }

        return group
    }
}