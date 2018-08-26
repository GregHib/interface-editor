package com.greg.model.cache.archives.widget

class WidgetData(@Transient var id: Int = -1) {

    //Default
    var x: Int = 0
    var y: Int = 0
    var parent: Int = 0
    var group: Int = 0
    var optionType: Int = 0
    var contentType: Int = 0
    var width: Int = 0
    var height: Int = 0
    var alpha: Byte = 0
    var hoverId: Int = 0
    var scriptDefaults: IntArray? = null
    var scriptOperators: IntArray? = null
    var scripts: Array<IntArray?>? = null

    //Container
    var scrollLimit: Int = 0
    var hidden: Boolean = false

    //Actions
    var hasActions: Boolean = false
    var actions: Array<String?>? = null

    //Appearance
    var centeredText: Boolean = false
    var fontIndex: Int = 0
    var shadowedText: Boolean = false

    //Children
    @Transient var childIndices: IntArray? = null
    @Transient var childX: IntArray? = null
    @Transient var childY: IntArray? = null
    var children: Array<WidgetData>? = null

    //Colour
    var defaultColour: Int = 0

    //Colours
    var defaultHoverColour: Int = 0
    var secondaryColour: Int = 0
    var secondaryHoverColour: Int = 0

    //Hover
    var hover: String = ""

    //Inventory
    var swappableItems: Boolean = false
    var usableItems: Boolean = false
    var replaceItems: Boolean = false
    var sprites: Array<SpriteData?>? = null
    var inventoryIds: IntArray? = null
    var inventoryAmounts: IntArray? = null

    //Rectangle
    var filled: Boolean = false

    //Model
    var defaultMediaType: Int = 0
    var defaultMedia: Int = 0
    var secondaryMediaType: Int = 0
    var secondaryMedia: Int = 0
    var defaultAnimationId: Int = 0
    var secondaryAnimationId: Int = 0
    var spriteScale: Int = 0
    var spritePitch: Int = 0
    var spriteRoll: Int = 0

    //Options
    var optionAttributes: Int = 0
    var optionText: String? = ""
    var optionCircumfix: String? = ""

    //Padding
    var spritePaddingX: Int = 0
    var spritePaddingY: Int = 0

    //Sprite
    var defaultSpriteIndex: Int? = null
    var defaultSpriteArchive: String? = null
    var secondarySpriteIndex: Int? = null
    var secondarySpriteArchive: String? = null

    //Text
    var defaultText: String? = ""
    var secondaryText: String? = ""

    override fun toString(): String {
        return Integer.toString(this.id)
    }

    fun clone(): WidgetData {
        val data = WidgetData(id)
        data.x = x
        data.y = y
        data.parent = parent
        data.group = group
        data.optionType = optionType
        data.contentType = contentType
        data.width = width
        data.height = height
        data.alpha = alpha
        data.hoverId = hoverId
        data.scriptDefaults = scriptDefaults
        data.scriptOperators = scriptOperators
        data.scripts = scripts
        data.scrollLimit = scrollLimit
        data.hidden = hidden
        data.hasActions = hasActions
        data.actions = actions
        data.centeredText = centeredText
        data.fontIndex = fontIndex
        data.shadowedText = shadowedText
        data.childIndices = childIndices
        data.childX = childX
        data.childY = childY
        data.children = children?.map { it.clone() }?.toTypedArray() ?: children
        data.defaultColour = defaultColour
        data.defaultHoverColour = defaultHoverColour
        data.secondaryColour = secondaryColour
        data.secondaryHoverColour = secondaryHoverColour
        data.hover = hover
        data.swappableItems = swappableItems
        data.usableItems = usableItems
        data.replaceItems = replaceItems
        data.sprites = sprites
        data.inventoryIds = inventoryIds
        data.inventoryAmounts = inventoryAmounts
        data.filled = filled
        data.defaultMediaType = defaultMediaType
        data.defaultMedia = defaultMedia
        data.secondaryMediaType = secondaryMediaType
        data.secondaryMedia = secondaryMedia
        data.defaultAnimationId = defaultAnimationId
        data.secondaryAnimationId = secondaryAnimationId
        data.spriteScale = spriteScale
        data.spritePitch = spritePitch
        data.spriteRoll = spriteRoll
        data.optionAttributes = optionAttributes
        data.optionText = optionText
        data.optionCircumfix = optionCircumfix
        data.spritePaddingX = spritePaddingX
        data.spritePaddingY = spritePaddingY
        data.defaultSpriteIndex = defaultSpriteIndex
        data.defaultSpriteArchive = defaultSpriteArchive
        data.secondarySpriteIndex = secondarySpriteIndex
        data.secondarySpriteArchive = secondarySpriteArchive
        data.defaultText = defaultText
        data.secondaryText = secondaryText
        return data
    }

    companion object {
        const val OPTION_OK = 1
        const val OPTION_USABLE = 2
        const val OPTION_CLOSE = 3
        const val OPTION_TOGGLE_SETTING = 4
        const val OPTION_RESET_SETTING = 5
        const val OPTION_CONTINUE = 6

        const val TYPE_CONTAINER = 0
        const val TYPE_MODEL_LIST = 1
        const val TYPE_INVENTORY = 2
        const val TYPE_RECTANGLE = 3
        const val TYPE_TEXT = 4
        const val TYPE_SPRITE = 5
        const val TYPE_MODEL = 6
        const val TYPE_ITEM_LIST = 7
    }
}
