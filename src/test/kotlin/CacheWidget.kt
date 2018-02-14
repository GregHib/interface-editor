/*
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Model
import io.nshusa.rsam.binary.sprite.Sprite
import java.nio.Buffer
import kotlin.experimental.and

class CacheWidget {

    var actions: Array<String>
    var alpha: Byte = 0
    var centerText: Boolean = false
    var children: IntArray
    var childX: IntArray
    var childY: IntArray
    var contentType: Int = 0
    var currentFrame: Int = 0
    var defaultAnimationId: Int = 0
    var defaultColour: Int = 0
    var defaultHoverColour: Int = 0
    var defaultMedia: Int = 0
    var defaultMediaType: Int = 0
    var defaultSprite: Sprite? = null
    var defaultText: String

    */
/**
     * Indicates whether or not the widget should be drawn filled, or just as an outline.
     *//*

    var filled: Boolean = false
    var font: Font
    var group: Int = 0
    var hasActions: Boolean = false
    var height: Int = 0
    var hidden: Boolean = false
    var horizontalDrawOffset: Int = 0
    var hover: String
    var hoverId: Int = 0
    var id: Int = 0
    var inventoryAmounts: IntArray
    var inventoryIds: IntArray
    var lastFrameTime: Int = 0
    var optionAttributes: Int = 0
    var optionCircumfix: String
    var optionText: String
    var optionType: Int = 0
    var parent: Int = 0
    var replaceItems: Boolean = false
    var scriptDefaults: IntArray
    var scriptOperators: IntArray
    var scripts: Array<IntArray>
    var scrollLimit: Int = 0
    var scrollPosition: Int = 0
    var secondaryAnimationId: Int = 0
    var secondaryColour: Int = 0
    var secondaryHoverColour: Int = 0
    var secondaryMedia: Int = 0
    var secondaryMediaType: Int = 0
    var secondarySprite: Sprite? = null
    var secondaryText: String
    var textShadow: Boolean = false
    var spritePaddingX: Int = 0
    var spritePaddingY: Int = 0
    var spritePitch: Int = 0
    var spriteRoll: Int = 0
    var sprites: Array<Sprite>
    var spriteScale: Int = 0
    var spriteX: IntArray
    var spriteY: IntArray
    var swappableItems: Boolean = false
    var usableItems: Boolean = false
    var verticalDrawOffset: Int = 0
    var width: Int = 0

    companion object {

        val OPTION_CLOSE = 3
        val OPTION_CONTINUE = 6
        val OPTION_OK = 1
        val OPTION_RESET_SETTING = 5
        val OPTION_TOGGLE_SETTING = 4
        val OPTION_USABLE = 2

        val TYPE_CONTAINER = 0
        val TYPE_INVENTORY = 2
        val TYPE_ITEM_LIST = 7
        val TYPE_MODEL = 6
        val TYPE_MODEL_LIST = 1
        val TYPE_RECTANGLE = 3
        val TYPE_SPRITE = 5
        val TYPE_TEXT = 4

        var widgets: Array<CacheWidget?>? = null

        fun load(interfaces: Archive, graphics: Archive?, fonts: Array<CacheFont>?) {
            val buffer = interfaces.readFile("data")
            widgets = arrayOfNulls((buffer.short and '\uffff'.toShort()).toInt())

            var parent = -1

            while (buffer.position() < buffer.) {
                var id = buffer.readUShort()
                if (id == 65535) {
                    parent = buffer.readUShort()
                    id = buffer.readUShort()
                }

                widgets[id] = CacheWidget()
                val widget = widgets[id]
                widget.id = id
                widget.parent = parent
                widget.group = buffer.readUByte()
                widget.optionType = buffer.readUByte()
                widget.contentType = buffer.readUShort()
                widget.width = buffer.readUShort()
                widget.height = buffer.readUShort()
                widget.alpha = buffer.readUByte()

                val hover = buffer.readUByte()
                widget.hoverId = if (hover != 0) hover - 1 shl 8 or buffer.readUByte() else -1

                val operators = buffer.readUByte()
                if (operators > 0) {
                    widget.scriptOperators = IntArray(operators)
                    widget.scriptDefaults = IntArray(operators)

                    for (index in 0 until operators) {
                        widget.scriptOperators[index] = buffer.readUByte()
                        widget.scriptDefaults[index] = buffer.readUShort()
                    }
                }

                val scripts = buffer.readUByte()
                if (scripts > 0) {
                    widget.scripts = arrayOfNulls(scripts)

                    for (script in 0 until scripts) {
                        val instructions = buffer.readUShort()
                        widget.scripts[script] = IntArray(instructions)

                        for (instruction in 0 until instructions) {
                            widget.scripts[script][instruction] = buffer.readUShort()
                        }
                    }
                }

                if (widget.group == TYPE_CONTAINER) {
                    widget.scrollLimit = buffer.readUShort()
                    widget.hidden = buffer.readUByte() === 1

                    val children = buffer.readUShort()
                    widget.children = IntArray(children)
                    widget.childX = IntArray(children)
                    widget.childY = IntArray(children)

                    for (index in 0 until children) {
                        widget.children[index] = buffer.readUShort()
                        widget.childX[index] = buffer.readShort()
                        widget.childY[index] = buffer.readShort()
                    }
                }

                if (widget.group == TYPE_MODEL_LIST) {
                    buffer.readUShort()
                    buffer.readUByte() // == 1
                }

                if (widget.group == TYPE_INVENTORY) {
                    widget.inventoryIds = IntArray(widget.width * widget.height)
                    widget.inventoryAmounts = IntArray(widget.width * widget.height)

                    widget.swappableItems = buffer.readUByte() === 1
                    widget.hasActions = buffer.readUByte() === 1
                    widget.usableItems = buffer.readUByte() === 1
                    widget.replaceItems = buffer.readUByte() === 1

                    widget.spritePaddingX = buffer.readUByte()
                    widget.spritePaddingY = buffer.readUByte()

                    widget.spriteX = IntArray(20)
                    widget.spriteY = IntArray(20)
                    widget.sprites = arrayOfNulls<Sprite>(20)

                    for (index in 0..19) {
                        val exists = buffer.readUByte()
                        if (exists == 1) {
                            widget.spriteX[index] = buffer.readShort()
                            widget.spriteY[index] = buffer.readShort()
                            val name = buffer.readString()

                            if (graphics != null && name.length > 0) {
                                val position = name.lastIndexOf(",")
                                widget.sprites[index] = getSprite(graphics,
                                        name.substring(0, position),
                                        Integer.parseInt(name.substring(position + 1)))
                            }
                        }
                    }

                    widget.actions = arrayOfNulls(5)
                    for (index in 0..4) {
                        widget.actions[index] = buffer.readString()

                        if (widget.actions[index].isEmpty()) {
                            widget.actions[index] = null
                        }
                    }
                }

                if (widget.group == TYPE_RECTANGLE) {
                    widget.filled = buffer.readUByte() === 1
                }

                if (widget.group == TYPE_TEXT || widget.group == TYPE_MODEL_LIST) {
                    widget.centerText = buffer.readUByte() === 1
                    val font = buffer.readUByte()

                    if (fonts != null) {
                        widget.font = fonts[font]
                    }

                    widget.textShadow = buffer.readUByte() === 1
                }

                if (widget.group == TYPE_TEXT) {
                    widget.defaultText = buffer.readString()
                    widget.secondaryText = buffer.readString()
                }

                if (widget.group == TYPE_MODEL_LIST || widget.group == TYPE_RECTANGLE
                        || widget.group == TYPE_TEXT) {
                    widget.defaultColour = buffer.readInt()
                }

                if (widget.group == TYPE_RECTANGLE || widget.group == TYPE_TEXT) {
                    widget.secondaryColour = buffer.readInt()
                    widget.defaultHoverColour = buffer.readInt()
                    widget.secondaryHoverColour = buffer.readInt()
                } else if (widget.group == TYPE_SPRITE) {
                    var name = buffer.readString()
                    if (graphics != null && name.length > 0) {
                        val index = name.lastIndexOf(",")
                        widget.defaultSprite = getSprite(graphics, name.substring(0, index),
                                Integer.parseInt(name.substring(index + 1)))
                    }

                    name = buffer.readString()
                    if (graphics != null && name.length > 0) {
                        val index = name.lastIndexOf(",")
                        widget.secondarySprite = getSprite(graphics, name.substring(0, index),
                                Integer.parseInt(name.substring(index + 1)))
                    }
                } else if (widget.group == TYPE_MODEL) {
                    var content = buffer.readUByte()
                    if (content != 0) {
                        widget.defaultMediaType = 1
                        widget.defaultMedia = (content - 1 shl 8) + buffer.readUByte()
                    }

                    content = buffer.readUByte()
                    if (content != 0) {
                        widget.secondaryMediaType = 1
                        widget.secondaryMedia = (content - 1 shl 8) + buffer.readUByte()
                    }

                    content = buffer.readUByte()
                    widget.defaultAnimationId = if (content != 0)
                        (content - 1 shl 8) + buffer.readUByte()
                    else
                        -1

                    content = buffer.readUByte()
                    widget.secondaryAnimationId = if (content != 0)
                        (content - 1 shl 8) + buffer.readUByte()
                    else
                        -1

                    widget.spriteScale = buffer.readUShort()
                    widget.spritePitch = buffer.readUShort()
                    widget.spriteRoll = buffer.readUShort()
                } else if (widget.group == TYPE_ITEM_LIST) {
                    widget.inventoryIds = IntArray(widget.width * widget.height)
                    widget.inventoryAmounts = IntArray(widget.width * widget.height)
                    widget.centerText = buffer.readUByte() === 1

                    val font = buffer.readUByte()
                    if (fonts != null) {
                        widget.font = fonts[font]
                    }

                    widget.textShadow = buffer.readUByte() === 1
                    widget.defaultColour = buffer.readInt()
                    widget.spritePaddingX = buffer.readShort()
                    widget.spritePaddingY = buffer.readShort()
                    widget.hasActions = buffer.readUByte() === 1
                    widget.actions = arrayOfNulls(5)

                    for (index in 0..4) {
                        widget.actions[index] = buffer.readString()

                        if (widget.actions[index].isEmpty()) {
                            widget.actions[index] = null
                        }
                    }
                }

                if (widget.optionType == OPTION_USABLE || widget.group == TYPE_INVENTORY) {
                    widget.optionCircumfix = buffer.readString()
                    widget.optionText = buffer.readString()
                    widget.optionAttributes = buffer.readUShort()
                }

                if (widget.optionType == OPTION_OK || widget.optionType == OPTION_TOGGLE_SETTING
                        || widget.optionType == OPTION_RESET_SETTING
                        || widget.optionType == OPTION_CONTINUE) {
                    widget.hover = buffer.readString()

                    if (widget.hover.isEmpty()) {
                        if (widget.optionType == OPTION_OK) {
                            widget.hover = "Ok"
                        } else if (widget.optionType == OPTION_TOGGLE_SETTING) {
                            widget.hover = "Select"
                        } else if (widget.optionType == OPTION_RESET_SETTING) {
                            widget.hover = "Select"
                        } else if (widget.optionType == OPTION_CONTINUE) {
                            widget.hover = "Continue"
                        }
                    }
                }
            }
        }

        private fun getSprite(archive: Archive?, name: String, id: Int): Sprite? {
            return null
        }
    }

}*/
