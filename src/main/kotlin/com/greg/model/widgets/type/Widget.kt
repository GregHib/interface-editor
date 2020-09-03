package com.greg.model.widgets.type

import com.greg.controller.canvas.DragContext
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.settings.Settings
import com.greg.model.widgets.JsonSerializer
import com.greg.model.widgets.Jsonable
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.Properties
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.model.widgets.properties.extended.StringProperty
import com.greg.model.widgets.type.groups.GroupHover
import com.greg.model.widgets.type.groups.GroupOptions
import com.greg.model.widgets.type.groups.GroupWidget
import rs.dusk.cache.definition.data.InterfaceComponentDefinition

open class Widget(builder: WidgetBuilder, id: Int) : GroupWidget(), Jsonable, GroupOptions, GroupHover {

    val type: WidgetType = builder.type
    val identifier = id
    val name: String = type.name.toLowerCase().capitalize()
    val dragContext = DragContext()

    val properties = Properties()

    override var x = IntProperty("x", Settings.getInt(Settings.DEFAULT_POSITION_X))
    override var y = IntProperty("y", Settings.getInt(Settings.DEFAULT_POSITION_Y))
    override var width = IntProperty("width", Settings.getInt(Settings.DEFAULT_RECTANGLE_WIDTH))
    override var height = IntProperty("height", Settings.getInt(Settings.DEFAULT_RECTANGLE_HEIGHT))

    override var widthBounds = ObjProperty("widthBounds", IntValues(Settings.getInt(Settings.DEFAULT_WIDGET_MINIMUM_WIDTH), Settings.getInt(Settings.WIDGET_CANVAS_WIDTH)))
    override var heightBounds = ObjProperty("heightBounds", IntValues(Settings.getInt(Settings.DEFAULT_WIDGET_MINIMUM_HEIGHT), Settings.getInt(Settings.WIDGET_CANVAS_HEIGHT)))

    override var locked = BoolProperty("locked", false)
    override var selected = BoolProperty("selected", false)
    override var invisible = BoolProperty("invisible", false)
    override var hidden = BoolProperty("hidden", false)
    override var hovered = BoolProperty("hovered", false)

    override var parent = ObjProperty<Widget?>("parent", null)
    override var typeProperty = IntProperty("optionType", 0)
    override var contentType = IntProperty("contentType", 0)
    override var alpha = IntProperty("alpha", 0)

    override var horizontalSizeModeProperty = IntProperty("horizontalSizeMode", 0)
    override var verticalSizeModeProperty = IntProperty("verticalSizeMode", 0)
    override var horizontalPositionModeProperty = IntProperty("horizontalPositionMode", 0)
    override var verticalPositionModeProperty = IntProperty("verticalPositionMode", 0)

    override var disableHoverProperty = BoolProperty("disableHover", false)
    override var applyTextProperty = StringProperty("applyText", "")

    override var optionsProperty: ObjProperty<Array<String>> = ObjProperty("scripts", emptyArray())

    var updateSelection = true

    init {
        properties.add(x, category = "Layout")
        properties.add(y, category = "Layout")
        if(builder.type != WidgetType.SPRITE && builder.type != WidgetType.INVENTORY) {
            properties.addRanged(width, widthBounds, "Layout")
            properties.addRanged(height, heightBounds, "Layout")
        }
        properties.addPanel(locked, false)
        properties.addPanel(selected, false)
        properties.addPanel(invisible, false)

        properties.add(horizontalSizeModeProperty, "Layout")
        properties.add(verticalSizeModeProperty, "Layout")
        properties.add(horizontalPositionModeProperty, "Layout")
        properties.add(verticalPositionModeProperty, "Layout")

        properties.add(disableHoverProperty, "Menu")
        properties.add(optionsProperty, "Menu")

        properties.add(applyTextProperty, "Text")
    }

    fun toData(): InterfaceComponentDefinition {
        return WidgetDataConverter.toData(this)
    }

    fun fromData(data: InterfaceComponentDefinition) {
        WidgetDataConverter.setData(this, data)
    }

    override fun fromJson(json: String) {
        val data = JsonSerializer.deserializer(json, InterfaceComponentDefinition::class.java) ?: return
        fromData(data)
    }

    override fun toJson(): String {
        return JsonSerializer.serialize(toData())
    }

    override fun toString(): String {
        return "$name ${identifier and 0xffff}"
    }

}