package com.greg.model.cache.archives.widget

import com.greg.controller.utils.ColourUtils
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.*
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import tornadofx.observable

object WidgetDataConverter {
    fun toData(widget: Widget): WidgetData {
        val data = WidgetData(widget.identifier)

        data.x = widget.getX()
        data.y = widget.getY()

        data.parent = widget.getParent()?.identifier ?: -1
        data.group = widget.type.ordinal
        data.optionType = widget.getOptionType()
        data.width = (widget as? WidgetInventory)?.getSlotWidth() ?: widget.getWidth()
        data.height = (widget as? WidgetInventory)?.getSlotHeight() ?: widget.getHeight()
        data.alpha = widget.getAlpha().toByte()
        data.hoverId = widget.getHoverId()

        data.scriptOperators = if (widget.getScriptOperators().isEmpty()) null else widget.getScriptOperators()
        data.scriptDefaults = if (widget.getScriptDefaults().isEmpty()) null else widget.getScriptDefaults()

        data.scripts = if (widget.getScripts().isEmpty()) null else widget.getScripts()

        if (widget is WidgetContainer) {
            data.scrollLimit = widget.getScrollLimit()
            data.hidden = widget.isHidden()
            data.children = widget.getChildren().map { it.toData() }.toTypedArray()
        }

        if (widget is WidgetInventory) {
            data.swappableItems = widget.getSwappableItems()
            data.hasActions = widget.hasActions()
            data.usableItems = widget.hasUsableItems()
            data.replaceItems = widget.isReplaceItems()
            data.spritePaddingX = widget.getSpritePaddingX()
            data.spritePaddingY = widget.getSpritePaddingY()
            data.sprites = widget.getSpritesArchive().mapIndexed { index, archive ->
                if(archive != null)
                SpriteData(widget.getSpriteX()[index], widget.getSpriteY()[index], "$archive,${widget.getSpritesIndex()[index]}")
                else null
            }.toTypedArray()
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

    fun setData(widget: Widget, data: WidgetData) {
        widget.setX(data.x)
        widget.setY(data.y)
        widget.setOptionType(data.optionType)
        widget.setContentType(data.contentType)
        if(widget is WidgetInventory) {
            widget.setSlotWidth(data.width)
            widget.setSlotHeight(data.height)
        } else {
            widget.setWidth(data.width)
            widget.setHeight(data.height)
        }
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
        }

        if (widget is WidgetInventory) {
            widget.setSwappableItems(data.swappableItems)
            widget.setHasActions(data.hasActions)
            widget.setUsableItems(data.usableItems)
            widget.setReplaceItems(data.replaceItems)
            widget.setSpritePaddingX(data.spritePaddingX)
            widget.setSpritePaddingY(data.spritePaddingY)

            val sprites = data.sprites
            if(sprites != null) {
                widget.setSpriteX(sprites.map { sprite -> sprite?.x ?: 0 }.toIntArray())
                widget.setSpriteY(sprites.map { sprite -> sprite?.y ?: 0 }.toIntArray())
                widget.setSpritesArchive(sprites.map { sprite ->
                    val name = sprite?.name ?: return@map null
                    if (name.isNotEmpty()) name.substring(0, name.lastIndexOf(",")) else null
                }.toTypedArray())
                widget.setSpritesIndex(sprites.map { sprite ->
                    val name = sprite?.name ?: return@map null
                    if (name.isNotEmpty()) Integer.parseInt(name.substring(name.lastIndexOf(",") + 1)) else null
                }.toTypedArray())
            }

            widget.setActions(data.actions!!)
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
            widget.setDefaultText(data.defaultText!!)
            widget.setSecondaryText(data.secondaryText!!)
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
            widget.setActions(data.actions!!)
        }

        if (widget.getOptionType() == WidgetData.OPTION_USABLE || widget is WidgetInventory) {
            widget.setOptionCircumfix(data.optionCircumfix!!)
            widget.setOptionText(data.optionText!!)
            widget.setOptionAttributes(data.optionAttributes)
        }

        if (widget.getOptionType() == WidgetData.OPTION_OK || widget.getOptionType() == WidgetData.OPTION_TOGGLE_SETTING || widget.getOptionType() == WidgetData.OPTION_RESET_SETTING || widget.getOptionType() == WidgetData.OPTION_CONTINUE) {
            widget.setHover(data.hover)
        }
    }

    /**
     * Creates Widget (and children) from {@link WidgetData.class}
     * @param data WidgetData to be converted
     * @param override overrides identifier, (only used for -1 to create unspecified id's)
     */
    fun create(data: WidgetData, override: Boolean = false): Widget {
        val widget = WidgetBuilder(WidgetType.values()[data.group]).build(if(override) -1 else data.id)

        setData(widget, data)
        if(widget is WidgetContainer) {
            val children = toChildren(data, override)
            if(children != null) {
                children.forEach { it.setParent(widget) }
                widget.setChildren(children.observable())
            }
        }

        return widget
    }

    private fun toChildren(parent: WidgetData, override: Boolean): ArrayList<Widget>? {
        return parent.children?.map { create(it, override) }?.toCollection(ArrayList())
    }
}