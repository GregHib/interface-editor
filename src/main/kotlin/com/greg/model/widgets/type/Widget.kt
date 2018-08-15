package com.greg.model.widgets.type

import com.greg.controller.canvas.DragContext
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.memento.Memento
import com.greg.model.widgets.memento.MementoBuilder
import com.greg.model.widgets.properties.Properties
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupHover
import com.greg.model.widgets.type.groups.GroupOptions
import com.greg.model.widgets.type.groups.GroupWidget
import javafx.beans.property.Property

open class Widget(builder: WidgetBuilder, id: Int) : GroupWidget(), GroupOptions, GroupHover {
    val type: WidgetType = builder.type
    val identifier = id
    val name: String = type.name.toLowerCase().capitalize()
    val dragContext = DragContext()

    val properties = Properties()

    override var x: IntProperty? = null
    override var y: IntProperty? = null
    override var width: IntProperty? = null
    override var height: IntProperty? = null

    override var widthBounds: ObjProperty<IntRange>? = null
    override var heightBounds: ObjProperty<IntRange>? = null

    override var locked: BoolProperty? = null
    override var selected: BoolProperty? = null
    override var invisible: BoolProperty? = null
    override var hidden: BoolProperty? = null
    override var hovered: BoolProperty? = null

    override var optionCircumfix: StringProperty? = null
    override var optionText: StringProperty? = null
    override var optionAttributes: IntProperty? = null
    override var hover: StringProperty? = null

    override var parent: IntProperty? = null
    override var optionType: IntProperty? = null
    override var contentType: IntProperty? = null
    override var alpha: IntProperty? = null
    override var hoverId: IntProperty? = null
    override var scriptOperators: ObjProperty<IntArray>? = null
    override var scriptDefaults: ObjProperty<IntArray>? = null
    override var scripts: ObjProperty<Array<IntArray?>>? = null

    var updateSelection = true

    init {
        properties.add(xProperty(), category = "Layout")
        properties.add(yProperty(), category = "Layout")
        if(builder.type != WidgetType.SPRITE) {
            properties.addCapped(widthProperty(), widthBoundsProperty(), "Layout")
            properties.addCapped(heightProperty(), heightBoundsProperty(), "Layout")
        }
        properties.addPanel(lockedProperty(), false)
        properties.addPanel(selectedProperty(), false)
        properties.addPanel(invisibleProperty(), false)
    }

    fun getMemento(): Memento {
        return MementoBuilder(this).build()
    }

    fun restore(memento: Memento) {
        for ((index, value) in properties.get().withIndex()) {
            if(value.property == selectedProperty())
                continue
            (value.property as Property<*>).value = memento.getValue(index, value.property)
        }
    }

    override fun toString(): String {
        return getMemento().toString()
    }
}