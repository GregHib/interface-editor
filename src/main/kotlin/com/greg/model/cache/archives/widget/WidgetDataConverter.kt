package com.greg.model.cache.archives.widget

import com.greg.controller.utils.ColourUtils
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.*
import rs.dusk.cache.definition.data.InterfaceComponentDefinition
import rs.dusk.cache.definition.data.InterfaceDefinition

object WidgetDataConverter {
    fun toData(widget: Widget): InterfaceComponentDefinition {
        val data = InterfaceComponentDefinition(widget.identifier)

        data.type = widget.getType()
        data.contentType = widget.getContentType()
        data.basePositionX = widget.getX()
        data.basePositionY = widget.getY()
        data.baseWidth = widget.getWidth()
        data.baseHeight = widget.getHeight()

        data.horizontalSizeMode = widget.horizontalSize.toByte()
        data.verticalSizeMode = widget.verticalSize.toByte()
        data.horizontalPositionMode = widget.horizontalPosition.toByte()
        data.verticalPositionMode = widget.verticalPosition.toByte()

        data.hidden = widget.isHidden()
        data.disableHover = !widget.hover
        data.applyText = widget.applyText
        data.options = widget.options

        data.alpha = widget.getAlpha()

        when (widget) {
            is WidgetContainer -> {
                data.scrollWidth = widget.scrollWidth
                data.scrollHeight = widget.scrollHeight
            }
            is WidgetRectangle -> {
                data.colour = ColourUtils.colourToRS(widget.getColour())
                data.filled = widget.isFilled()
                data.alpha = widget.getAlpha()
            }
            is WidgetText -> {
                data.fontId = widget.getFont()
                data.monochrome = widget.monochrome
                data.text = widget.getText()
                data.lineHeight = widget.lineHeight
                data.horizontalTextAlign = widget.horizontalAlign
                data.verticalTextAlign = widget.verticalAlign
                data.shaded = widget.isShaded()
                data.colour = ColourUtils.colourToRS(widget.getColour())
                data.lineCount = widget.lineCount
            }
            is WidgetSprite -> {
                data.defaultImage = widget.getSprite()
                data.imageRotation = widget.rotation
                data.imageRepeat = widget.repeat
                data.alpha = widget.getAlpha()
                data.rotation = widget.orientation
                data.backgroundColour = ColourUtils.colourToRS(widget.getSecondaryColour())
                data.flipVertical = widget.flipVertical
                data.flipHorizontal = widget.flipHorizontal
                data.colour = ColourUtils.colourToRS(widget.getColour())
            }
            is WidgetModel -> {
                data.defaultMediaType = widget.getDefaultMediaType()
                data.defaultMediaId = widget.getDefaultMedia()
                data.centreType = widget.isCentred()
                data.ignoreZBuffer = !widget.depthBuffer
                data.viewportX = widget.viewportX
                data.viewportY = widget.viewportY
                data.spritePitch = widget.getSpritePitch()
                data.spriteRoll = widget.getSpriteRoll()
                data.spriteYaw = widget.spriteYaw
                data.spriteScale = widget.getSpriteScale()
                data.animation = widget.getAnimation()
                data.viewportWidth = widget.viewportWidth
                data.viewportHeight = widget.viewportHeight
            }
            is WidgetLine -> {
                data.lineWidth = widget.lineWidth
                data.colour = ColourUtils.colourToRS(widget.getColour())
                data.lineMirrored = widget.lineMirrored
            }
        }
        return data
    }

    fun setData(widget: Widget, data: InterfaceComponentDefinition) {
        widget.setType(data.type)
        widget.setContentType(data.contentType)
        widget.setX(data.basePositionX)
        widget.setY(data.basePositionY)
        widget.setWidth(data.baseWidth)
        widget.setHeight(data.baseHeight)

        widget.horizontalSize = data.horizontalSizeMode.toInt()
        widget.verticalSize = data.verticalSizeMode.toInt()
        widget.horizontalPosition = data.horizontalPositionMode.toInt()
        widget.verticalPosition = data.verticalPositionMode.toInt()

        widget.setHidden(data.hidden)
        widget.hover = !data.disableHover
        widget.applyText = data.applyText
        widget.options = data.options ?: emptyArray()

        widget.setAlpha(data.alpha)

        when (widget) {
            is WidgetContainer -> {
                widget.scrollWidth = data.scrollWidth
                widget.scrollHeight = data.scrollHeight
            }
            is WidgetRectangle -> {
                widget.setColour(ColourUtils.getColour(data.colour))
                widget.setFilled(data.filled)
                widget.setAlpha(data.alpha)
            }
            is WidgetText -> {
                widget.setFont(data.fontId)
                widget.monochrome = data.monochrome
                widget.setText(data.text)
                widget.lineHeight = data.lineHeight
                widget.horizontalAlign = data.horizontalTextAlign
                widget.verticalAlign = data.verticalTextAlign
                widget.setShaded(data.shaded)
                widget.setColour(ColourUtils.getColour(data.colour))
                widget.lineCount = data.lineCount
            }
            is WidgetSprite -> {
                widget.setSprite(data.defaultImage)
                widget.rotation = data.imageRotation
                widget.repeat = data.imageRepeat
                widget.setAlpha(data.alpha)
                widget.orientation = data.rotation
                widget.setSecondaryColour(ColourUtils.getColour(data.backgroundColour))
                widget.flipVertical = data.flipVertical
                widget.flipHorizontal = data.flipHorizontal
                widget.setColour(ColourUtils.getColour(data.colour))
            }
            is WidgetModel -> {
                widget.setDefaultMediaType(data.defaultMediaType)
                widget.setDefaultMedia(data.defaultMediaId)
                widget.setCentred(data.centreType)
                widget.depthBuffer = !data.ignoreZBuffer
                widget.viewportX = data.viewportX
                widget.viewportY = data.viewportY
                widget.setSpritePitch(data.spritePitch)
                widget.setSpriteRoll(data.spriteRoll)
                widget.spriteYaw = data.spriteYaw
                widget.setSpriteScale(data.spriteScale)
                widget.setAnimation(data.animation)
                widget.viewportWidth = data.viewportWidth
                widget.viewportHeight = data.viewportHeight
            }
            is WidgetLine -> {
                widget.lineWidth = data.lineWidth
                widget.setColour(ColourUtils.getColour(data.colour))
                widget.lineMirrored = data.lineMirrored
            }
        }
    }

    fun create(data: InterfaceDefinition, override: Boolean = false): Widget {
        val root: WidgetContainer = WidgetBuilder(WidgetType.CONTAINER).build(if (override) -1 else data.id) as WidgetContainer
        root.setLocked(true)

        val components = data.components
        root.setWidth(getWidth(components))
        root.setHeight(getHeight(components))

        if (components == null) {
            return root
        }

        val p = (data.id shl 16)
        val all = components.map { (id, component) ->
            id or p to create(component)
        }.toMap()

        all.forEach { (id, widget) ->
            val component = components[id and 0xffff] ?: return@forEach
            val parentComponent = component.parent and 0xffff
            val parent = if (parentComponent != 65535) {
                all[parentComponent or p] as WidgetContainer
            } else {
                root
            }
            val list = parent.getChildren()
            list.add(widget)
            widget.setParent(parent)
            parent.setChildren(list)
        }
        return root
    }

    private fun getWidth(components: Map<Int, InterfaceComponentDefinition>?): Int {
        val max = components?.maxBy { it.value.basePositionX + it.value.baseWidth } ?: return Settings.getInt(Settings.WIDGET_CANVAS_WIDTH)
        return max.value.basePositionX + max.value.baseWidth
    }

    private fun getHeight(components: Map<Int, InterfaceComponentDefinition>?): Int {
        val max = components?.maxBy { it.value.basePositionY + it.value.baseHeight } ?: return Settings.getInt(Settings.WIDGET_CANVAS_HEIGHT)
        return max.value.basePositionY + max.value.baseHeight
    }

    fun create(data: InterfaceComponentDefinition, override: Boolean = false): Widget {
        val widget = WidgetBuilder(WidgetType.forIndex(data.type)).build(if (override) -1 else data.id)
        setData(widget, data)
        if (widget is WidgetContainer || widget is WidgetSprite) {
            widget.setLocked(true)
        }
        return widget
    }

}