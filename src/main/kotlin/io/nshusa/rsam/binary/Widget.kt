package io.nshusa.rsam.binary

import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.graphics.render.Raster
import io.nshusa.rsam.util.ByteBufferUtils
import io.nshusa.rsam.util.HashUtils
import io.nshusa.rsam.util.RenderUtils
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.IOException
import java.util.*
import kotlin.experimental.and

class Widget {
    lateinit var actions: Array<String?>
    var alpha: Byte = 0
    var centeredText: Boolean = false
    var children: IntArray? = null
    lateinit var childX: IntArray
    lateinit var childY: IntArray
    var contentType: Int = 0
    var currentFrame: Int = 0
    var defaultAnimationId: Int = 0
    var defaultColour: Int = 0
    var defaultHoverColour: Int = 0
    var defaultMedia: Int = 0
    var defaultMediaType: Int = 0
    var defaultSprite: Sprite? = null
    lateinit var defaultText: String
    var filled: Boolean = false
    lateinit var font: Font
    var group: Int = 0
    var hasActions: Boolean = false
    var height: Int = 0
    var hidden: Boolean = false
    var horizontalDrawOffset: Int = 0
    lateinit var hover: String
    var hoverId: Int = 0
    var id: Int = 0
    lateinit var inventoryAmounts: IntArray
    lateinit var inventoryIds: IntArray
    var lastFrameTime: Int = 0
    var optionAttributes: Int = 0
    lateinit var optionCircumfix: String
    lateinit var optionText: String
    var optionType: Int = 0
    var parent: Int = 0
    var replaceItems: Boolean = false
    lateinit var scriptDefaults: IntArray
    lateinit var scriptOperators: IntArray
    lateinit var scripts: Array<IntArray?>
    var scrollLimit: Int = 0
    var scrollPosition: Int = 0
    var secondaryAnimationId: Int = 0
    var secondaryColour: Int = 0
    var secondaryHoverColour: Int = 0
    var secondaryMedia: Int = 0
    var secondaryMediaType: Int = 0
    var secondarySprite: Sprite? = null
    lateinit var secondaryText: String
    var shadowedText: Boolean = false
    var spritePaddingX: Int = 0
    var spritePaddingY: Int = 0
    var spritePitch: Int = 0
    var spriteRoll: Int = 0
    lateinit var sprites: Array<Sprite?>
    var spriteScale: Int = 0
    lateinit var spriteX: IntArray
    lateinit var spriteY: IntArray
    var swappableItems: Boolean = false
    var usableItems: Boolean = false
    var verticalDrawOffset: Int = 0
    var width: Int = 0

    constructor() {
        this.id = -1
    }

    constructor(id: Int) {
        this.id = id
        if (id >= 0 && id < widgets!!.size) {
            if (widgets!![id] != null) {
                println(String.format("overriding widget: %d", id))
            }

            widgets!![id] = this
        } else {
            throw IllegalArgumentException(String.format("widget=%d must be between 0 and %d", id, widgets!!.size))
        }
    }

    fun toBufferedImage(): BufferedImage? {
        if (this.width > 0 && this.height > 0) {
            Raster.init(this.height, this.width, IntArray(this.width * this.height))
            Raster.reset()
            if (this.group == 0) {
                RenderUtils.renderWidget(this, 0, 0, 0)
            } else if (this.group == 5) {
                if (this.defaultSprite != null) {
                    this.defaultSprite!!.drawSprite(0, 0)
                }
            } else if (this.group == 4) {
                RenderUtils.renderText(this, 0, 0)
            } else if (this.group == 3) {
                RenderUtils.renderText(this, 0, 0)
            }

            val data = Raster.raster
            val bimage = BufferedImage(Raster.width, Raster.height, 1)
            val pixels = (bimage.raster.dataBuffer as DataBufferInt).data
            System.arraycopy(data, 0, pixels, 0, data.size)
            return bimage
        } else {
            return null
        }
    }

    fun swapInventoryItems(first: Int, second: Int) {
        var tmp = this.inventoryIds[first]
        this.inventoryIds[first] = this.inventoryIds[second]
        this.inventoryIds[second] = tmp
        tmp = this.inventoryAmounts[first]
        this.inventoryAmounts[first] = this.inventoryAmounts[second]
        this.inventoryAmounts[second] = tmp
    }

    override fun toString(): String {
        return Integer.toString(this.id)
    }

    companion object {
        private const val OPTION_OK = 1
        private const val OPTION_USABLE = 2
        private const val OPTION_CLOSE = 3
        private const val OPTION_TOGGLE_SETTING = 4
        private const val OPTION_RESET_SETTING = 5
        private const val OPTION_CONTINUE = 6

        private const val TYPE_CONTAINER = 0
        private const val TYPE_MODEL_LIST = 1
        private const val TYPE_INVENTORY = 2
        private const val TYPE_RECTANGLE = 3
        private const val TYPE_TEXT = 4
        private const val TYPE_SPRITE = 5
        private const val TYPE_MODEL = 6
        private const val TYPE_ITEM_LIST = 7

        private var widgets: Array<Widget?>? = null
        private val spriteCache = HashMap<Long, Sprite>()


        @Throws(IOException::class)
        fun decode(interfaces: Archive, graphics: Archive?, fonts: Array<Font>?) {
            val buffer = interfaces.readFile("data")
            widgets = arrayOfNulls(buffer.short.toInt() and '\uffff'.toInt())
            var parent = -1

            while (true) {
                var widget: Widget
                do {
                    if (buffer.position() >= buffer.remaining()) {
                        return
                    }

                    var id = buffer.short.toInt() and '\uffff'.toInt()
                    if (id == 65535) {
                        parent = buffer.short.toInt() and '\uffff'.toInt()
                        id = buffer.short.toInt() and '\uffff'.toInt()
                    }

                    widget = Widget(id)
                    widget.parent = parent
                    widget.group = buffer.get().toInt() and 255
                    widget.optionType = buffer.get().toInt() and 255
                    widget.contentType = buffer.short.toInt() and '\uffff'.toInt()
                    widget.width = buffer.short.toInt() and '\uffff'.toInt()
                    widget.height = buffer.short.toInt() and '\uffff'.toInt()
                    widget.alpha = buffer.get() and 255.toByte()
                    val hover = buffer.get() and 255.toByte()
                    widget.hoverId = if (hover != 0.toByte()) hover - 1 shl 8 or (buffer.get().toInt() and 255) else -1

                    val operators = buffer.get().toInt() and 255
                    var scripts: Int
                    if (operators > 0) {
                        widget.scriptOperators = IntArray(operators)
                        widget.scriptDefaults = IntArray(operators)

                        scripts = 0
                        while (scripts < operators) {
                            widget.scriptOperators[scripts] = buffer.get().toInt() and 255
                            widget.scriptDefaults[scripts] = buffer.short.toInt() and '\uffff'.toInt()
                            scripts++
                        }
                    }

                    scripts = buffer.get().toInt() and 255
                    var font: Int
                    var index: Int
                    if (scripts > 0) {
                        widget.scripts = arrayOfNulls(scripts)

                        font = 0
                        while (font < scripts) {
                            index = buffer.short.toInt() and '\uffff'.toInt()
                            widget.scripts[font] = IntArray(index)

                            for (instruction in 0 until index) {
                                widget.scripts[font]!![instruction] = buffer.short.toInt() and '\uffff'.toInt()
                            }
                            font++
                        }
                    }

                    if (widget.group == TYPE_CONTAINER) {
                        widget.scrollLimit = buffer.short.toInt() and '\uffff'.toInt()
                        widget.hidden = buffer.get().toInt() and 255 == 1
                        font = buffer.short.toInt() and '\uffff'.toInt()
                        widget.children = IntArray(font)
                        widget.childX = IntArray(font)
                        widget.childY = IntArray(font)

                        index = 0
                        while (index < font) {
                            widget.children!![index] = buffer.short.toInt() and '\uffff'.toInt()
                            widget.childX[index] = buffer.short.toInt()
                            widget.childY[index] = buffer.short.toInt()
                            index++
                        }
                    }

                    if (widget.group == TYPE_MODEL_LIST) {
                        buffer.short
                        buffer.get()
                    }

                    if (widget.group == TYPE_INVENTORY) {
                        widget.inventoryIds = IntArray(widget.width * widget.height)
                        widget.inventoryAmounts = IntArray(widget.width * widget.height)
                        widget.swappableItems = buffer.get().toInt() and 255 == 1
                        widget.hasActions = buffer.get().toInt() and 255 == 1
                        widget.usableItems = buffer.get().toInt() and 255 == 1
                        widget.replaceItems = buffer.get().toInt() and 255 == 1
                        widget.spritePaddingX = buffer.get().toInt() and 255
                        widget.spritePaddingY = buffer.get().toInt() and 255
                        widget.spriteX = IntArray(20)
                        widget.spriteY = IntArray(20)
                        widget.sprites = arrayOfNulls(20)

                        font = 0
                        while (font < 20) {
                            index = buffer.get().toInt() and 255
                            if (index == 1) {
                                widget.spriteX[font] = buffer.short.toInt()
                                widget.spriteY[font] = buffer.short.toInt()
                                val name = ByteBufferUtils.getString(buffer)
                                if (graphics != null && name.isNotEmpty()) {
                                    val position = name.lastIndexOf(",")
                                    widget.sprites[font] = getSprite(graphics, name.substring(0, position), Integer.parseInt(name.substring(position + 1)))
                                }
                            }
                            font++
                        }

                        widget.actions = arrayOfNulls(5)

                        font = 0
                        while (font < 5) {
                            widget.actions[font] = ByteBufferUtils.getString(buffer)
                            if (widget.actions[font]!!.isEmpty()) {
                                widget.actions[font] = null
                            }
                            font++
                        }
                    }

                    if (widget.group == TYPE_RECTANGLE) {
                        widget.filled = buffer.get().toInt() and 255 == 1
                    }

                    if (widget.group == TYPE_TEXT || widget.group == TYPE_MODEL_LIST) {
                        widget.centeredText = buffer.get().toInt() and 255 == 1
                        font = buffer.get().toInt() and 255
                        if (fonts != null) {
                            widget.font = fonts[font]
                        }

                        widget.shadowedText = buffer.get().toInt() and 255 == 1
                    }

                    if (widget.group == TYPE_TEXT) {
                        widget.defaultText = ByteBufferUtils.getString(buffer)
                        widget.secondaryText = ByteBufferUtils.getString(buffer)
                    }

                    if (widget.group == TYPE_MODEL_LIST || widget.group == TYPE_RECTANGLE || widget.group == TYPE_TEXT) {
                        widget.defaultColour = buffer.int
                    }

                    if (widget.group == TYPE_RECTANGLE || widget.group == TYPE_TEXT) {
                        widget.secondaryColour = buffer.int
                        widget.defaultHoverColour = buffer.int
                        widget.secondaryHoverColour = buffer.int
                    }

                    if (widget.group == TYPE_SPRITE) {
                        var name = ByteBufferUtils.getString(buffer)
                        if (graphics != null && name.isNotEmpty()) {
                            index = name.lastIndexOf(",")
                            widget.defaultSprite = getSprite(graphics, name.substring(0, index), Integer.parseInt(name.substring(index + 1)))
                        }

                        name = ByteBufferUtils.getString(buffer)
                        if (graphics != null && name.isNotEmpty()) {
                            index = name.lastIndexOf(",")
                            widget.secondarySprite = getSprite(graphics, name.substring(0, index), Integer.parseInt(name.substring(index + 1)))
                        }
                    }

                    if (widget.group == TYPE_MODEL) {
                        font = buffer.get().toInt() and 255
                        if (font != 0) {
                            widget.defaultMediaType = 1
                            widget.defaultMedia = (font - 1 shl 8) + buffer.get() and 255
                        }

                        font = buffer.get().toInt() and 255
                        if (font != 0) {
                            widget.secondaryMediaType = 1
                            widget.secondaryMedia = (font - 1 shl 8) + buffer.get() and 255
                        }

                        font = buffer.get().toInt() and 255
                        widget.defaultAnimationId = if (font != 0) (font - 1 shl 8) + buffer.get() and 255 else -1
                        font = buffer.get().toInt() and 255
                        widget.secondaryAnimationId = if (font != 0) (font - 1 shl 8) + buffer.get() and 255 else -1
                        widget.spriteScale = buffer.short.toInt() and '\uffff'.toInt()
                        widget.spritePitch = buffer.short.toInt() and '\uffff'.toInt()
                        widget.spriteRoll = buffer.short.toInt() and '\uffff'.toInt()
                    }

                    if (widget.group == TYPE_ITEM_LIST) {
                        widget.inventoryIds = IntArray(widget.width * widget.height)
                        widget.inventoryAmounts = IntArray(widget.width * widget.height)
                        widget.centeredText = buffer.get().toInt() and 255 == 1
                        font = buffer.get().toInt() and 255
                        if (fonts != null) {
                            widget.font = fonts[font]
                        }

                        widget.shadowedText = buffer.get().toInt() and 255 == 1
                        widget.defaultColour = buffer.int
                        widget.spritePaddingX = buffer.short.toInt()
                        widget.spritePaddingY = buffer.short.toInt()
                        widget.hasActions = buffer.get().toInt() and 255 == 1
                        widget.actions = arrayOfNulls(5)

                        index = 0
                        while (index < 5) {
                            widget.actions[index] = ByteBufferUtils.getString(buffer)
                            if (widget.actions[index]!!.isEmpty()) {
                                widget.actions[index] = null
                            }
                            index++
                        }
                    }

                    if (widget.optionType == OPTION_USABLE || widget.group == TYPE_INVENTORY) {
                        widget.optionCircumfix = ByteBufferUtils.getString(buffer)
                        widget.optionText = ByteBufferUtils.getString(buffer)
                        widget.optionAttributes = buffer.short.toInt() and '\uffff'.toInt()
                    }

                } while (widget.optionType != OPTION_OK && widget.optionType != OPTION_TOGGLE_SETTING && widget.optionType != OPTION_RESET_SETTING && widget.optionType != OPTION_CONTINUE)

                widget.hover = ByteBufferUtils.getString(buffer)
                if (widget.hover.isEmpty()) {
                    when {
                        widget.optionType == OPTION_OK -> widget.hover = "Ok"
                        widget.optionType == OPTION_TOGGLE_SETTING -> widget.hover = "Select"
                        widget.optionType == OPTION_RESET_SETTING -> widget.hover = "Select"
                        widget.optionType == OPTION_CONTINUE -> widget.hover = "Continue"
                    }
                }
            }
        }

        private fun getSprite(archive: Archive, name: String, id: Int): Sprite? {
            val key = HashUtils.hashSpriteName(name) shl 8 or id.toLong()
            var sprite: Sprite? = spriteCache[key]
            return sprite ?: try {
                sprite = Sprite.decode(archive, name, id)
                spriteCache[key] = sprite
                sprite
            } catch (var7: Exception) {
                null
            }
        }

        fun lookup(id: Int): Widget? {
            return if (widgets == null) null else widgets!![id]
        }

        fun count(): Int {
            return if (widgets == null) 0 else widgets!!.size
        }
    }
}
