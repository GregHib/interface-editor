package com.greg.model.widgets.type

import com.greg.controller.canvas.DragContext
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.widgets.JsonSerializer
import com.greg.model.widgets.Jsonable
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.properties.Properties
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupHover
import com.greg.model.widgets.type.groups.GroupOptions
import com.greg.model.widgets.type.groups.GroupWidget

open class Widget(builder: WidgetBuilder, id: Int) : GroupWidget(), Jsonable, GroupOptions, GroupHover {

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

    fun toData(): WidgetData {
        return WidgetDataConverter.toData(this)
    }

    fun fromData(data: WidgetData) {
        WidgetDataConverter.setData(this, data)
    }

    override fun fromJson(json: String) {
        val data = JsonSerializer.deserializer(json, WidgetData::class.java) ?: return
        fromData(data)
    }

    override fun toJson(): String {
        return JsonSerializer.serialize(toData())
    }

    override fun toString(): String {
        return toJson()
    }

}