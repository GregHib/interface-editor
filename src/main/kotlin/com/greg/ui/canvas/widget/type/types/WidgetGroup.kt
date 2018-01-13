package com.greg.ui.canvas.widget.type.types

import com.greg.ui.action.change.ChangeType
import com.greg.ui.canvas.widget.Widget
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.builder.WidgetBuilder
import com.greg.ui.canvas.widget.builder.data.WidgetData
import com.greg.ui.canvas.widget.memento.MementoBuilder
import com.greg.ui.canvas.widget.memento.mementoes.Memento
import com.greg.ui.panel.Panel
import com.greg.ui.panel.panels.PanelType
import com.greg.ui.panel.panels.attribute.Attribute
import com.greg.ui.panel.panels.attribute.AttributeType
import com.greg.ui.panel.panels.attribute.column.Column
import com.greg.ui.panel.panels.attribute.column.rows.RowBuilder
import javafx.beans.value.ObservableValue


class WidgetGroup(builder: WidgetBuilder, id: Int) : WidgetData(builder, id) {

    init {
        addToStart(this)
        attributes.addLayout("Location X", "layoutXProperty", AttributeType.NUMBER_FIELD)
        attributes.addLayout("Location Y", "layoutYProperty", AttributeType.NUMBER_FIELD)
    }


    /**
     * Initialisation
     * - Adds change listener to widget attribute value change
     */

    fun init(groups: List<Column>, widgets: Widgets) {
        for (group in groups) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget ->
                            for (type in PanelType.values())
                                initGroup(group, widget, type, widgets)
                    }
        }
    }

    private fun initGroup(group: Column, widget: Widget, type: PanelType, widgets: Widgets) {
        widget.getAttributes(type)?.forEachIndexed { index, attribute ->
            //If attribute is a property (All are?)
            if (attribute.isProperty()) {
                val prop = attribute.getProperty(widget) as ObservableValue<*>
                //Add listener so linked attribute is updated every time value is changed
                prop.addListener { _, _, newValue ->
                    if(!attribute.ignoreListener) {
                        group.rows[index].elements.last().refresh(newValue)
                        widgets.record(ChangeType.CHANGE, this)
                    } else {
                        attribute.ignoreListener = false
                    }
                }
            }
        }
    }


    /**
     * Attribute linking
     * - Links panel attribute to change correct widget
     */

    fun link(panel: Panel, widgets: Widgets) {
        for (group in panel.groups!!) {
            components
                    .filter { widget -> group.widgetClass == widget::class }
                    .forEach { widget -> linkGroup(group, widget, panel.type, widgets) }
        }
    }

    private fun linkGroup(group: Column, widget: Widget, type: PanelType, widgets: Widgets) {
        widget.getAttributes(type)?.forEachIndexed { index, attribute ->
            //Add this widget to the list of outputs for the property row
            val row = group.rows[index]
            //First as currently only supports the first linkable element
            row.elements.first().link({ value ->
                if(attribute.type.convert(attribute.getValue(widget)) != value) {
                    //Start must happen before value change to capture original value
                    widgets.start(this)
                    //Ignore must be set before as setValue fires the listener
                    attribute.ignoreListener = true
                    //Change the attributes value
                    attribute.setValue(widget, value)
                    //Record value change
                    widgets.record(ChangeType.CHANGE, this)

                    //Finish action
                    widgets.finish()
                }
            })
        }
    }


    /**
     * Group creation
     * - Creates Column
     */

    fun getGroups(type: PanelType): List<Column>? {
        val list = mutableListOf<Column>()
        for (component in components) {
            val attributes = component.getAttributes(type)
            if (attributes != null && attributes.isNotEmpty())
                list.add(createGroup(component::class.simpleName!!, component, attributes))
        }
        return list
    }

    private fun createGroup(name: String, widget: Widget, attributes: List<Attribute>): Column {
        //Create a new group
        val group = Column(name, widget::class)

        for (attribute in attributes) {
            //Get the attribute's current value via reflection
            val value = attribute.getValue(widget)

            //Create and add row
            val builder = RowBuilder(attribute.title)
            builder.addAttribute(attribute.type, value)
            group.add(builder.build())
        }

        return group
    }

    fun getMemento(): Memento {
        return MementoBuilder(this).build()
    }

    fun restore(memento: Memento) {
        var index = 0
        for (component in components.reversed()) {
            PanelType.values()
                    .mapNotNull { component.getAttributes(it) }
                    .filter { it.isNotEmpty() }
                    .flatMap { it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.title })) }
                    .forEach { attribute ->
                        if(index < memento.values.size)
                            attribute.setValue(component, attribute.type.convert(memento.values[index++]))
                    }
        }
    }
}