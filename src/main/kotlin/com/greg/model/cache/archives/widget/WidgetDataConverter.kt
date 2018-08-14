package com.greg.model.cache.archives.widget

import com.greg.controller.utils.ColourUtils
import com.greg.model.cache.archives.ArchiveInterface
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.*
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours

object WidgetDataConverter {
    fun toData(widget: Widget): WidgetData {
        val data = WidgetData(widget.identifier)

        data.parent = widget.getParent()
        data.group = widget.type.ordinal
        data.optionType = widget.getOptionType()
        data.width = widget.getWidth()
        data.height = widget.getHeight()
        data.alpha = widget.getAlpha().toByte()
        data.hoverId = widget.getHoverId()

        data.scriptOperators = if (widget.getScriptOperators().isEmpty()) null else widget.getScriptOperators()
        data.scriptDefaults = if (widget.getScriptDefaults().isEmpty()) null else widget.getScriptDefaults()

        data.scripts = if (widget.getScripts().isEmpty()) null else widget.getScripts()

        if (widget is WidgetContainer) {
            data.scrollLimit = widget.getScrollLimit()
            data.hidden = widget.isHidden()

            val size = widget.getChildren().size
            data.children = IntArray(size)
            data.childX = IntArray(size)
            data.childY = IntArray(size)

            widget.getChildren().forEachIndexed { index, widget ->
                data.children!![index] = widget.identifier
                data.childX[index] = widget.getX()
                data.childY[index] = widget.getY()
            }
        }

        if (widget is WidgetInventory) {
            data.swappableItems = widget.getSwappableItems()
            data.hasActions = widget.hasActions()
            data.usableItems = widget.hasUsableItems()
            data.replaceItems = widget.isReplaceItems()
            data.spritePaddingX = widget.getSpritePaddingX()
            data.spritePaddingY = widget.getSpritePaddingY()

            data.sprites = widget.getSprites()
            data.spriteX = widget.getSpriteX()
            data.spriteY = widget.getSpriteY()
            data.spritesArchive = widget.getSpritesArchive()
            data.spritesIndex = widget.getSpritesIndex()
            data.actions = widget.getActions()
        }

        if (widget is WidgetRectangle) {
            data.filled = widget.isFilled()
        }

        if ((widget is WidgetText || widget is WidgetModelList) && widget is GroupAppearance) {
            data.centeredText = widget.isCentred()
            data.fontIndex = widget.getFontIndex()
            data.shadowedText = widget.hasShadow()
        }

        if (widget is WidgetText) {
            data.defaultText = widget.getDefaultText()
            data.secondaryText = widget.getSecondaryText()
        }

        if ((widget is WidgetModelList || widget is WidgetRectangle || widget is WidgetText)&& widget is GroupColour) {
            data.defaultColour = ColourUtils.colourToRS(widget.getDefaultColour())
        }

        if ((widget is WidgetRectangle || widget is WidgetText) && widget is GroupColours) {
            data.secondaryColour = ColourUtils.colourToRS(widget.getSecondaryColour())
            data.defaultHoverColour = ColourUtils.colourToRS(widget.getDefaultHoverColour())
            data.secondaryHoverColour = ColourUtils.colourToRS(widget.getSecondaryHoverColour())
        }

        if (widget is WidgetSprite) {
            data.defaultSpriteArchive = if(widget.getDefaultSpriteArchive().isEmpty()) null else widget.getDefaultSpriteArchive()
            data.defaultSpriteIndex = widget.getDefaultSprite()
            data.secondarySpriteArchive = if(widget.getSecondarySpriteArchive().isEmpty()) null else widget.getSecondarySpriteArchive()
            data.secondarySpriteIndex = widget.getSecondarySprite()
        }


        if (widget is WidgetModel) {
            data.defaultMediaType = widget.getDefaultMediaType()
            data.defaultMedia = widget.getDefaultMedia()
            data.secondaryMediaType = widget.getSecondaryMediaType()
            data.secondaryMedia= widget.getSecondaryMedia()
            data.defaultAnimationId = widget.getDefaultAnimationId()
            data.secondaryAnimationId = widget.getSecondaryAnimationId()
            data.spriteScale = widget.getSpriteScale()
            data.spritePitch = widget.getSpritePitch()
            data.spriteRoll = widget.getSpriteRoll()
        }


        if (widget is WidgetItemList) {
            data.centeredText = widget.isCentred()
            data.fontIndex = widget.getFontIndex()
            data.shadowedText = widget.hasShadow()
            data.defaultColour = ColourUtils.colourToRS(widget.getDefaultColour())
            data.spritePaddingX = widget.getSpritePaddingX()
            data.spritePaddingY = widget.getSpritePaddingY()
            data.hasActions = widget.hasActions()
            data.actions = widget.getActions()
        }

        if (widget.getOptionType() == WidgetData.OPTION_USABLE || widget is WidgetInventory) {
            data.optionCircumfix = widget.getOptionCircumfix()
            data.optionText = widget.getOptionText()
            data.optionAttributes = widget.getOptionAttributes()
        }

        if (widget.getOptionType() == WidgetData.OPTION_OK || widget.getOptionType() == WidgetData.OPTION_TOGGLE_SETTING || widget.getOptionType() == WidgetData.OPTION_RESET_SETTING || widget.getOptionType() == WidgetData.OPTION_CONTINUE) {
            data.hover = widget.getHover()
        }

        return data
    }

    private fun create(data: WidgetData, childX: Int, childY: Int): Widget {
        val widget = WidgetBuilder(WidgetType.values()[data.group]).build(data.id)

        widget.setParent(data.parent)

        widget.setOptionType(data.optionType)
        widget.setContentType(data.contentType)
        widget.setWidth(data.width)
        widget.setHeight(data.height)
        widget.setAlpha(data.alpha.toInt())

        widget.setHoverId(data.hoverId)

        if(data.scriptOperators != null)
            widget.setScriptOperators(data.scriptOperators!!)
        if(data.scriptDefaults != null)
            widget.setScriptDefaults(data.scriptDefaults!!)
        if(data.scripts != null)
            widget.setScripts(data.scripts!!)

        if(widget is WidgetContainer) {
            widget.setScrollLimit(data.scrollLimit)
            widget.setHidden(data.hidden)
            widget.setChildren(toChildren(data))
        }

        if (widget is WidgetInventory) {
            widget.setSwappableItems(data.swappableItems)
            widget.setHasActions(data.hasActions)
            widget.setUsableItems(data.usableItems)
            widget.setReplaceItems(data.replaceItems)
            widget.setSpritePaddingX(data.spritePaddingX)
            widget.setSpritePaddingY(data.spritePaddingY)

            widget.setSpriteX(data.spriteX)
            widget.setSpriteY(data.spriteY)
            widget.setSprites(data.sprites)
            widget.setSpritesArchive(data.spritesArchive)
            widget.setSpritesIndex(data.spritesIndex)

            widget.setActions(data.actions)
        }

        if (widget is WidgetRectangle) {
            widget.setFilled(data.filled)
        }

        if ((widget is WidgetText || widget is WidgetModelList) && widget is GroupAppearance) {
            widget.setCentred(data.centeredText)
            widget.setFontIndex(data.fontIndex)
            widget.setShadow(data.shadowedText)
        }

        if (widget is WidgetText) {
            widget.setDefaultText(data.defaultText)
            widget.setSecondaryText(data.secondaryText)
        }

        if ((widget is WidgetModelList || widget is WidgetRectangle || widget is WidgetText)&& widget is GroupColour) {
            widget.setDefaultColour(ColourUtils.getColour(data.defaultColour))
        }

        if ((widget is WidgetRectangle || widget is WidgetText) && widget is GroupColours) {
            widget.setSecondaryColour(ColourUtils.getColour(data.secondaryColour))
            widget.setDefaultHoverColour(ColourUtils.getColour(data.defaultHoverColour))
            widget.setSecondaryHoverColour(ColourUtils.getColour(data.secondaryHoverColour))
        }

        if (widget is WidgetSprite) {
            if(!data.defaultSpriteArchive.isNullOrEmpty())
                widget.setDefaultSpriteArchive(data.defaultSpriteArchive!!)
            if(data.defaultSpriteIndex != null)
                widget.setDefaultSprite(data.defaultSpriteIndex!!, false)

            if(!data.secondarySpriteArchive.isNullOrEmpty())
                widget.setSecondarySpriteArchive(data.secondarySpriteArchive!!)
            if(data.secondarySpriteIndex != null)
                widget.setSecondarySprite(data.secondarySpriteIndex!!, false)
        }

        if (widget is WidgetModel) {
            if(data.defaultMediaType == 1) {
                widget.setDefaultMediaType(data.defaultMediaType)
                widget.setDefaultMedia(data.defaultMedia)
            }
            if(data.secondaryMediaType == 1) {
                widget.setSecondaryMediaType(data.secondaryMediaType)
                widget.setSecondaryMedia(data.secondaryMedia)
            }

            widget.setDefaultAnimationId(data.defaultAnimationId)
            widget.setSecondaryAnimationId(data.secondaryAnimationId)
            widget.setSpriteScale(data.spriteScale)
            widget.setSpritePitch(data.spritePitch)
            widget.setSpriteRoll(data.spriteRoll)
        }

        if (widget is WidgetItemList) {
            widget.setCentred(data.centeredText)
            widget.setFontIndex(data.fontIndex)
            widget.setShadow(data.shadowedText)
            widget.setDefaultColour(ColourUtils.getColour(data.defaultColour))
            widget.setSpritePaddingX(data.spritePaddingX)
            widget.setSpritePaddingY(data.spritePaddingY)
            widget.setHasActions(data.hasActions)
            widget.setActions(data.actions)
        }

        if (widget.getOptionType() == WidgetData.OPTION_USABLE || widget is WidgetInventory) {
            widget.setOptionCircumfix(data.optionCircumfix)
            widget.setOptionText(data.optionText)
            widget.setOptionAttributes(data.optionAttributes)
        }

        if (widget.getOptionType() == WidgetData.OPTION_OK || widget.getOptionType() == WidgetData.OPTION_TOGGLE_SETTING || widget.getOptionType() == WidgetData.OPTION_RESET_SETTING || widget.getOptionType() == WidgetData.OPTION_CONTINUE) {
            widget.setHover(data.hover)
        }

        widget.setX(childX)
        widget.setY(childY)

        return widget
    }

    private fun toChildren(parent: WidgetData): ArrayList<Widget> {
        val children = arrayListOf<Widget>()

        val len = parent.children?.size ?: return children

        for (id in 0 until len) {
            val data = ArchiveInterface.lookup(parent.children!![id]) ?: continue

            children.add(create(data, parent.childX[id], parent.childY[id]))
        }
        return children
    }

    fun toChildren(parent: WidgetData, x: Int = 0, y: Int = 0): Widget {
        return create(parent, x, y)
    }
}