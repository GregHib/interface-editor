package com.greg.model.cache.archives.widget

import com.greg.controller.utils.ColourUtils
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.*
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import rs.dusk.cache.definition.data.InterfaceComponentDefinition
import rs.dusk.cache.definition.data.InterfaceDefinition

object WidgetDataConverter {
    fun toData(widget: Widget): InterfaceComponentDefinition {
        val data = InterfaceComponentDefinition(widget.identifier)

        data.basePositionX = widget.getX()
        data.basePositionY = widget.getY()

        data.parent = widget.getParent()?.identifier ?: -1
        data.type = widget.type.ordinal
        data.contentType = widget.contentType.get()
        data.baseWidth = (widget as? WidgetInventory)?.getSlotWidth() ?: widget.getWidth()
        data.baseHeight = (widget as? WidgetInventory)?.getSlotHeight() ?: widget.getHeight()
        data.alpha = widget.getAlpha()

        if (widget is WidgetContainer) {
            data.hidden = widget.isHidden()
        }

        if (widget is WidgetRectangle) {
            data.filled = widget.isFilled()
        }

        if ((widget is WidgetText || widget is WidgetModelList) && widget is GroupAppearance) {
            data.centreType = widget.isCentred()
            data.fontId = widget.getFontIndex()
            data.shaded = widget.hasShadow()
        }

        if (widget is WidgetText) {
            data.text = widget.getDefaultText()
            data.applyText = widget.getSecondaryText()
        }

        if ((widget is WidgetModelList || widget is WidgetRectangle || widget is WidgetText)&& widget is GroupColour) {
            data.colour = ColourUtils.colourToRS(widget.getDefaultColour())
        }

        if ((widget is WidgetRectangle || widget is WidgetText) && widget is GroupColours) {
            data.backgroundColour = ColourUtils.colourToRS(widget.getSecondaryColour())
        }

        if (widget is WidgetSprite) {
            data.defaultImage = widget.getDefaultSpriteArchive()
            data.imageRepeat = widget.getRepeatsImage()
        }


        if (widget is WidgetModel) {
            data.defaultMediaType = widget.getDefaultMediaType()
            data.defaultMediaId = widget.getDefaultMedia()
            data.animation = widget.getDefaultAnimationId()
            data.spriteScale = widget.getSpriteScale()
            data.spritePitch = widget.getSpritePitch()
            data.spriteRoll = widget.getSpriteRoll()
        }


        if (widget is WidgetItemList) {
            data.centreType = widget.isCentred()
            data.fontId = widget.getFontIndex()
            data.shaded = widget.hasShadow()
            data.colour = ColourUtils.colourToRS(widget.getDefaultColour())
            data.options = widget.getActions()
        }

        return data
    }

    fun setData(widget: Widget, data: InterfaceComponentDefinition) {
        widget.setX(data.basePositionX)
        widget.setY(data.basePositionY)
        widget.setOptionType(data.type)
        widget.setContentType(data.contentType)
        if (widget !is WidgetInventory) {
            widget.setWidth(data.baseWidth)
            widget.setHeight(data.baseHeight)
        }
        widget.setAlpha(data.alpha)

        if(widget is WidgetContainer) {
            widget.setHidden(data.hidden)
        }

        if (widget is WidgetInventory) {

            val sprites = data.defaultImage
            if(sprites != -1) {
                widget.setSpritesIndex(intArrayOf(sprites))
            }

            data.options?.let { options ->
                widget.setActions(options)
            }
        }

        if (widget is WidgetRectangle) {
            widget.setFilled(data.filled)
        }

        if ((widget is WidgetText || widget is WidgetModelList) && widget is GroupAppearance) {
            widget.setCentred(!data.centreType)
            widget.setFontIndex(data.fontId)
            widget.setShadow(data.shaded)
        }

        if (widget is WidgetText) {
            widget.setDefaultText(data.text)
            widget.setSecondaryText(data.applyText)
        }

        if ((widget is WidgetModelList || widget is WidgetRectangle || widget is WidgetText)&& widget is GroupColour) {
            widget.setDefaultColour(ColourUtils.getColour(data.colour))
        }

        if ((widget is WidgetRectangle || widget is WidgetText) && widget is GroupColours) {
            widget.setSecondaryColour(ColourUtils.getColour(data.backgroundColour))
        }

        if (widget is WidgetSprite) {

            widget.setDefaultSpriteArchive(data.defaultImage)
            widget.setRepeats(data.imageRepeat)
        }

        if (widget is WidgetModel) {
            if(data.defaultMediaType == 1) {
                widget.setDefaultMediaType(data.defaultMediaType)
                widget.setDefaultMedia(data.defaultMediaId)
            }

            widget.setDefaultAnimationId(data.animation)
            widget.setSpriteScale(data.spriteScale)
            widget.setSpritePitch(data.spritePitch)
            widget.setSpriteRoll(data.spriteRoll)
        }

        if (widget is WidgetItemList) {
            widget.setCentred(data.centreType)
            widget.setFontIndex(data.fontId)
            widget.setShadow(data.shaded)
            widget.setDefaultColour(ColourUtils.getColour(data.colour))
            widget.setHasActions(data.options != null)
            if(data.options != null) {
                widget.setActions(data.options!!)
            }
        }
    }

    fun create(data: InterfaceDefinition, override: Boolean = false): Widget {
        val root: WidgetContainer = WidgetBuilder(WidgetType.CONTAINER).build(if(override) -1 else data.id) as WidgetContainer
        root.setLocked(true)
        root.setWidth(765)
        root.setHeight(503)
        val components = data.components ?: return root
        val p = (data.id shl 16)
        val all = components.map { (id, component) ->
            id or p to create(component)
        }.toMap()

        all.forEach { (id, widget) ->
            val component = components[id and 0xffff] ?: return@forEach
            val parentComponent = component.parent and 0xffff
            val parent = if(parentComponent != 65535) {
                all[parentComponent or p] as WidgetContainer
            } else {
                root
            }
            val list = parent.getChildren()
            list.add(widget)
            parent.setChildren(list)
        }
        return root
    }

    fun create(data: InterfaceComponentDefinition, override: Boolean = false): Widget {
        val widget = WidgetBuilder(WidgetType.forIndex(data.type)).build(if(override) -1 else data.id)
        setData(widget, data)
        if(widget is WidgetContainer || widget is WidgetSprite) {
            widget.setLocked(true)
        }
        return widget
    }

}