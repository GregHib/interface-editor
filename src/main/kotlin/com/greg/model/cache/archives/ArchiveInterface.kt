package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.Widget
import rs.dusk.cache.Cache
import rs.dusk.cache.definition.decoder.InterfaceDecoder

class ArchiveInterface : CacheArchive() {

    companion object {

        internal lateinit var decoder: InterfaceDecoder
        private var widgetsData = arrayOfNulls<WidgetData>(Short.MAX_VALUE.toInt())

        fun lookup(id: Int): WidgetData? {
            var widget = widgetsData[id]
            if (widget != null) {
                return widget
            }
            val def = decoder.getOrNull(id) ?: return null
            val children = def.components?.map { (componentId, component) ->
                WidgetData(componentId).apply {
                    parent = id
                    group = component.type
                    x = component.basePositionX
                    y = component.basePositionY
                    width = component.baseWidth
                    height = component.baseHeight
                    alpha = component.alpha.toByte()
                    hidden = component.hidden
                    centeredText = !component.centreType
                    shadowedText = component.shaded
                    defaultColour = component.colour
                    secondaryColour = component.backgroundColour
                    filled = component.filled
                    defaultText = component.text
                    secondaryText = component.applyText
                    spriteScale = component.spriteScale
                    spritePitch = component.spritePitch
                    spriteRoll = component.spriteRoll
                    repeats = component.imageRepeat
                    if (component.defaultImage != -1) {
                        defaultSpriteArchive = component.defaultImage
                        defaultSpriteIndex = 0
                    }
                }
            }
            widget = WidgetData(id).apply {
                this.width = maxWidth(children)
                this.height = maxHeight(children)
                this.children = children?.toTypedArray()
            }
            widgetsData[id] = widget
            return widget
        }

        fun updateData(widget: Widget) {
            widgetsData[widget.identifier] = widget.toData()
        }

        private fun clear() {
            widgetsData = arrayOfNulls(Short.MAX_VALUE.toInt())
        }

        fun set(widgets: Array<WidgetData?>) {
            clear()
            widgets.filterNotNull().forEachIndexed { index, data ->
                widgetsData[index] = data
            }
        }

        fun set(widgets: WidgetData) {
            widgetsData[widgets.id] = widgets
        }

        fun get(): Array<WidgetData?> {
            return widgetsData
        }

        fun maxWidth(children: List<WidgetData>?): Int {
            val highest = children?.maxBy { it.x + it.width } ?: return Settings.getInt(Settings.DEFAULT_RECTANGLE_WIDTH)
            return highest.x + highest.width
        }

        fun maxHeight(children: List<WidgetData>?): Int {
            val highest = children?.maxBy { it.y + it.height } ?: return Settings.getInt(Settings.DEFAULT_RECTANGLE_HEIGHT)
            return highest.y + highest.height
        }
    }

    fun save(widgets: WidgetsController, cache: Cache): Boolean {
        return false
    }

    override fun reset(): Boolean {
        clear()
        return true
    }

    override fun load(cache: Cache): Boolean {
        decoder = InterfaceDecoder(cache)
        println("Found ${decoder.size} interfaces.")
        return true
    }

    fun display(widgets: WidgetsController, index: Int) {
        val data = lookup(index) ?: return

        if (data.group != WidgetData.TYPE_CONTAINER || data.children?.isEmpty() ?: return)
            return

        val widget = WidgetDataConverter.create(data)
        widgets.add(widget)
    }
}