package com.greg.canvas.widget

import com.greg.panels.attributes.Attribute
import com.greg.panels.attributes.AttributeType
import com.greg.panels.attributes.parts.AttributeGroup
import com.greg.panels.attributes.parts.AttributeRowBuilder
import com.greg.panels.attributes.parts.pane.AttributePane
import com.greg.panels.attributes.parts.pane.AttributePaneType
import javafx.beans.value.ObservableValue


class Widget(builder: WidgetBuilder) : WidgetData(builder) {

    init {
        addToStart(this)
        attributes.addLayout("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD)
        attributes.addLayout("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD)
    }


    /**
     * Initialisation
     * - Adds change listener to widget attribute value change
     */

    fun init(groups: List<AttributeGroup>) {
        for (group in groups) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget ->
                            for (type in AttributePaneType.values())
                                initGroup(group, widget, type)
                    }
        }
    }

    private fun initGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)?.forEachIndexed { index, attribute ->
            //If attribute is a property (All are?)
            if (attribute.isProperty()) {
                val prop = attribute.getProperty(widget) as ObservableValue<*>
                //Add listener so linked attribute is updated every time value is changed
                prop.addListener { _, _, newValue -> group.rows[index].linkableList.last().refresh(newValue) }
            }
        }
    }


    /**
     * Attribute linking
     * - Links panel attribute to change correct widget
     */

    fun link(pane: AttributePane) {
        for (group in pane.groups!!) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget -> linkGroup(group, widget, pane.type) }
        }
    }

    private fun linkGroup(group: AttributeGroup, widget: WidgetInterface, type: AttributePaneType) {
        widget.getAttributes(type)?.forEachIndexed { index, attribute ->
            //Add this widget to the list of outputs for the property row
            val row = group.rows[index]
            //First as currently only supports 1 linkable
            row.linkableList.first().link({ value -> attribute.setValue(widget, value) })
        }
    }

    /**
     * Group creation
     * - Creates AttributeGroup
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