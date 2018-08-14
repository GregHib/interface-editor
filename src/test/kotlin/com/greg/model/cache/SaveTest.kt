package com.greg.model.cache

import com.greg.model.cache.archives.ArchiveInterface
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Widget
import io.nshusa.rsam.util.ByteBufferUtils
import java.nio.ByteBuffer
import kotlin.experimental.and

class SaveTest {
    val cache = Cache(CachePath("./cache/"))
    val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

    fun load(buffer: ByteBuffer): Boolean {
        return try {

            ArchiveInterface.widgets = arrayOfNulls(buffer.short.toInt() and 0xffff)

            var parent = -1

            var widget: Widget
            while (buffer.hasRemaining()) {
                var id = buffer.short.toInt() and 0xffff
                if (id == 65535) {
                    parent = buffer.short.toInt() and 0xffff
                    id = buffer.short.toInt() and 0xffff
                }

                widget = Widget(id)
                widget.parent = parent
                widget.group = buffer.get().toInt() and 255
                widget.optionType = buffer.get().toInt() and 255
                widget.contentType = buffer.short.toInt() and 0xffff
                widget.width = buffer.short.toInt() and 0xffff
                widget.height = buffer.short.toInt() and 0xffff
                widget.alpha = buffer.get() and 255.toByte()
                val hover = buffer.get() and 255.toByte()
                val h2 = if (hover != 0.toByte()) buffer.get().toInt() else -1

                widget.hoverId = if (hover != 0.toByte()) hover - 1 shl 8 or (h2 and 255) else -1

                val operators = buffer.get().toInt() and 255
                var scripts: Int
                if (operators > 0) {
                    widget.scriptOperators = IntArray(operators)
                    widget.scriptDefaults = IntArray(operators)

                    scripts = 0
                    while (scripts < operators) {
                        widget.scriptOperators!![scripts] = buffer.get().toInt() and 255
                        widget.scriptDefaults[scripts] = buffer.short.toInt() and 0xffff
                        scripts++
                    }
                }

                scripts = buffer.get().toInt() and 255
                var font: Int
                var index: Int
                if (scripts > 0) {
                    widget.scripts = arrayOfNulls(scripts)

                    var index = 0
                    while (index < scripts) {
                        val length = buffer.short.toInt() and 0xffff
                        widget.scripts!![index] = IntArray(length)

                        for (instruction in 0 until length) {
                            widget.scripts!![index]!![instruction] = buffer.short.toInt() and 0xffff
                        }
                        index++
                    }
                }

                if (widget.group == Widget.TYPE_CONTAINER) {
                    widget.scrollLimit = buffer.short.toInt() and 0xffff
                    widget.hidden = buffer.get().toInt() and 255 == 1
                    font = buffer.short.toInt() and 0xffff
                    widget.children = IntArray(font)
                    widget.childX = IntArray(font)
                    widget.childY = IntArray(font)

                    index = 0
                    while (index < font) {
                        widget.children!![index] = buffer.short.toInt() and 0xffff
                        widget.childX[index] = buffer.short.toInt()
                        widget.childY[index] = buffer.short.toInt()
                        index++
                    }
                }

                if (widget.group == Widget.TYPE_MODEL_LIST) {
                    buffer.short
                    buffer.get()
                }

                if (widget.group == Widget.TYPE_INVENTORY) {
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
                    widget.spritesArchive = arrayOfNulls(20)
                    widget.spritesIndex = arrayOfNulls(20)

                    font = 0
                    while (font < 20) {
                        index = buffer.get().toInt() and 255
                        if (index == 1) {
                            widget.spriteX[font] = buffer.short.toInt()
                            widget.spriteY[font] = buffer.short.toInt()
                            val name = ByteBufferUtils.getString(buffer)
                            widget.sprites[font] = name
                            if (name.isNotEmpty()) {
                                val position = name.lastIndexOf(",")
                                widget.spritesArchive[font] = name.substring(0, position)
                                widget.spritesIndex[font] = Integer.parseInt(name.substring(position + 1))
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

                if (widget.group == Widget.TYPE_RECTANGLE) {
                    widget.filled = buffer.get().toInt() and 255 == 1
                }

                if (widget.group == Widget.TYPE_TEXT || widget.group == Widget.TYPE_MODEL_LIST) {
                    widget.centeredText = buffer.get().toInt() and 255 == 1
                    font = buffer.get().toInt() and 255
                    widget.fontIndex = font

                    widget.shadowedText = buffer.get().toInt() and 255 == 1
                }

                if (widget.group == Widget.TYPE_TEXT) {
                    widget.defaultText = ByteBufferUtils.getString(buffer).replace("RuneScape", "Run Escape")
                    widget.secondaryText = ByteBufferUtils.getString(buffer)
                }

                if (widget.group == Widget.TYPE_MODEL_LIST || widget.group == Widget.TYPE_RECTANGLE || widget.group == Widget.TYPE_TEXT) {
                    widget.defaultColour = buffer.int
                }

                if (widget.group == Widget.TYPE_RECTANGLE || widget.group == Widget.TYPE_TEXT) {
                    widget.secondaryColour = buffer.int
                    widget.defaultHoverColour = buffer.int
                    widget.secondaryHoverColour = buffer.int
                }

                if (widget.group == Widget.TYPE_SPRITE) {
                    var name = ByteBufferUtils.getString(buffer)

                    if (name.isNotEmpty()) {
                        index = name.lastIndexOf(",")
                        widget.defaultSpriteArchive = name.substring(0, index)
                        widget.defaultSpriteIndex = Integer.parseInt(name.substring(index + 1))
                    }

                    name = ByteBufferUtils.getString(buffer)
                    if (name.isNotEmpty()) {
                        index = name.lastIndexOf(",")
                        widget.secondarySpriteArchive = name.substring(0, index)
                        widget.secondarySpriteIndex = Integer.parseInt(name.substring(index + 1))
                    }
                }

                if (widget.group == Widget.TYPE_MODEL) {
                    font = buffer.get().toInt() and 255
                    if (font != 0) {
                        widget.defaultMediaType = 1
                        widget.defaultMedia = (font - 1 shl 8) + (buffer.get().toInt() and 255)
                    }

                    font = buffer.get().toInt() and 255
                    if (font != 0) {
                        widget.secondaryMediaType = 1
                        widget.secondaryMedia = (font - 1 shl 8) + (buffer.get().toInt() and 255)
                    }

                    font = buffer.get().toInt() and 255
                    widget.defaultAnimationId = if (font != 0) (font - 1 shl 8) + (buffer.get().toInt() and 255) else -1
                    font = buffer.get().toInt() and 255
                    widget.secondaryAnimationId = if (font != 0) (font - 1 shl 8) + (buffer.get().toInt() and 255) else -1
                    widget.spriteScale = buffer.short.toInt() and 0xffff
                    widget.spritePitch = buffer.short.toInt() and 0xffff
                    widget.spriteRoll = buffer.short.toInt() and 0xffff
                }

                if (widget.group == Widget.TYPE_ITEM_LIST) {
                    widget.inventoryIds = IntArray(widget.width * widget.height)
                    widget.inventoryAmounts = IntArray(widget.width * widget.height)
                    widget.centeredText = buffer.get().toInt() and 255 == 1
                    font = buffer.get().toInt() and 255
                    widget.fontIndex = font

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

                if (widget.optionType == Widget.OPTION_USABLE || widget.group == Widget.TYPE_INVENTORY) {
                    widget.optionCircumfix = ByteBufferUtils.getString(buffer)
                    widget.optionText = ByteBufferUtils.getString(buffer)
                    widget.optionAttributes = buffer.short.toInt() and 0xffff
                }

                if (widget.optionType == Widget.OPTION_OK || widget.optionType == Widget.OPTION_TOGGLE_SETTING || widget.optionType == Widget.OPTION_RESET_SETTING || widget.optionType == Widget.OPTION_CONTINUE) {
                    widget.hover = ByteBufferUtils.getString(buffer)

                    if (widget.hover.isEmpty()) {
                        widget.hover = when (widget.optionType) {
                            Widget.OPTION_OK -> "Ok"
                            Widget.OPTION_TOGGLE_SETTING -> "Select"
                            Widget.OPTION_RESET_SETTING -> "Select"
                            Widget.OPTION_CONTINUE -> "Continue"
                            else -> widget.hover
                        }
                    }
                }

                /*val newBuffer = write(widget)

                val current = Arrays.copyOfRange(buffer.array(), start, buffer.position()).toTypedArray()
                val new = newBuffer.payload.toArray()

                if (!Arrays.equals(current, new)) {
                    println("Widget ${widget.id} ${widget.parent} ${widget.group} ${widget.optionType}")
                    println(Arrays.deepToString(current))
                    println(Arrays.deepToString(new))
                    return true
                }*/

                ArchiveInterface.widgets!![id] = widget
            }

            true
        } catch (e: NullPointerException) {
            e.printStackTrace()
            cache.reset()
            false
        }
    }

    fun write(): Buffer {

        val buffer = Buffer()

        buffer.writeShort(ArchiveInterface.widgets!!.requireNoNulls().size)

        ArchiveInterface.widgets!!.requireNoNulls().forEach { widget ->
            buffer.writeShort(if (widget.id == widget.parent) -1 else widget.id)

            if (widget.id == widget.parent) {
                buffer.writeShort(widget.parent)
                buffer.writeShort(widget.id)
            }

            buffer.writeByte(widget.group)
            buffer.writeByte(widget.optionType)
            buffer.writeShort(widget.contentType)
            buffer.writeShort(widget.width)
            buffer.writeShort(widget.height)
            buffer.writeByte(widget.alpha)

            if (widget.hoverId == -1) {
                buffer.writeByte(0)
            } else {
                buffer.writeByte((widget.hoverId shr 8) + 1)
                buffer.writeByte(widget.hoverId and 255)
            }

            buffer.writeByte(widget.scriptOperators?.size ?: 0)

            if (widget.scriptOperators?.isNotEmpty() == true) {
                widget.scriptOperators!!.forEachIndexed { index, i ->
                    buffer.writeByte(i)
                    buffer.writeShort(widget.scriptDefaults[index])
                }
            }

            buffer.writeByte(widget.scripts?.size ?: 0)

            if (widget.scripts?.isNotEmpty() == true) {
                widget.scripts!!.requireNoNulls().forEach { script ->
                    buffer.writeShort(script.size)

                    script.forEach { int ->
                        buffer.writeShort(int)
                    }
                }
            }

            if (widget.group == Widget.TYPE_CONTAINER) {
                buffer.writeShort(widget.scrollLimit)
                buffer.writeByte(if (widget.hidden) 1 else 0)
                buffer.writeShort(widget.children?.size ?: 0)
                widget.children?.forEachIndexed { index, child ->
                    buffer.writeShort(child)
                    buffer.writeShort(widget.childX[index])
                    buffer.writeShort(widget.childY[index])
                }
            }

            if (widget.group == Widget.TYPE_MODEL_LIST) {
                buffer.writeShort(0)
                buffer.writeInt(0)
            }

            if (widget.group == Widget.TYPE_INVENTORY) {
                buffer.writeByte(if (widget.swappableItems) 1 else 0)
                buffer.writeByte(if (widget.hasActions) 1 else 0)
                buffer.writeByte(if (widget.usableItems) 1 else 0)
                buffer.writeByte(if (widget.replaceItems) 1 else 0)
                buffer.writeByte(widget.spritePaddingX)
                buffer.writeByte(widget.spritePaddingY)

                widget.sprites.forEachIndexed { index, s ->
                    buffer.writeByte(if (s == null) 0 else 1)
                    if (s != null) {
                        buffer.writeShort(widget.spriteX[index])
                        buffer.writeShort(widget.spriteY[index])

                        if (widget.spritesArchive[index] == null && widget.spritesIndex[index] == null)
                            buffer.writeString("")
                        else
                            buffer.writeString("${widget.spritesArchive[index]},${widget.spritesIndex[index]}")
                    }
                }

                widget.actions.forEach { buffer.writeString(it ?: "") }
            }

            if (widget.group == Widget.TYPE_RECTANGLE) {
                buffer.writeByte(if (widget.filled) 1 else 0)
            }

            if (widget.group == Widget.TYPE_TEXT || widget.group == Widget.TYPE_MODEL_LIST) {
                buffer.writeByte(if (widget.centeredText) 1 else 0)
                buffer.writeByte(widget.fontIndex)
                buffer.writeByte(if (widget.shadowedText) 1 else 0)
            }

            if (widget.group == Widget.TYPE_TEXT) {
                buffer.writeString(widget.defaultText)
                buffer.writeString(widget.secondaryText)
            }

            if (widget.group == Widget.TYPE_MODEL_LIST || widget.group == Widget.TYPE_RECTANGLE || widget.group == Widget.TYPE_TEXT) {
                buffer.writeInt(widget.defaultColour)
            }

            if (widget.group == Widget.TYPE_RECTANGLE || widget.group == Widget.TYPE_TEXT) {
                buffer.writeInt(widget.secondaryColour)
                buffer.writeInt(widget.defaultHoverColour)
                buffer.writeInt(widget.secondaryHoverColour)
            }

            if (widget.group == Widget.TYPE_SPRITE) {
                if (widget.defaultSpriteArchive.isNullOrEmpty())
                    buffer.writeString("")
                else
                    buffer.writeString("${widget.defaultSpriteArchive},${widget.defaultSpriteIndex}")

                if (widget.secondarySpriteArchive.isNullOrEmpty())
                    buffer.writeString("")
                else
                    buffer.writeString("${widget.secondarySpriteArchive},${widget.secondarySpriteIndex}")
            }

            if (widget.group == Widget.TYPE_MODEL) {
                if (widget.defaultMediaType != 1) {
                    buffer.writeByte(0)
                } else {
                    buffer.writeByte((widget.defaultMedia shr 8) + 1)
                    buffer.writeByte(widget.defaultMedia and 255)
                }

                if (widget.secondaryMediaType != 1) {
                    buffer.writeByte(0)
                } else {
                    buffer.writeByte((widget.secondaryMedia shr 8) + 1)
                    buffer.writeByte(widget.secondaryMedia and 255)
                }

                if (widget.defaultAnimationId == -1) {
                    buffer.writeByte(0)
                } else {
                    buffer.writeByte((widget.defaultAnimationId shr 8) + 1)
                    buffer.writeByte(widget.defaultAnimationId and 255)
                }
                if (widget.secondaryAnimationId == -1) {
                    buffer.writeByte(0)
                } else {
                    buffer.writeByte((widget.secondaryAnimationId shr 8) + 1)
                    buffer.writeByte(widget.secondaryAnimationId and 255)
                }

                buffer.writeShort(widget.spriteScale)
                buffer.writeShort(widget.spritePitch)
                buffer.writeShort(widget.spriteRoll)
            }

            if (widget.group == Widget.TYPE_ITEM_LIST) {
                buffer.writeByte(if (widget.centeredText) 1 else 0)
                buffer.writeByte(widget.fontIndex)


                buffer.writeByte(if (widget.shadowedText) 1 else 0)
                buffer.writeInt(widget.defaultColour)
                buffer.writeShort(widget.spritePaddingX)
                buffer.writeShort(widget.spritePaddingY)
                buffer.writeByte(if (widget.hasActions) 1 else 0)

                widget.actions.forEach { buffer.writeString(it ?: "") }
            }

            if (widget.optionType == Widget.OPTION_USABLE || widget.group == Widget.TYPE_INVENTORY) {
                buffer.writeString(widget.optionCircumfix)
                buffer.writeString(widget.optionText)
                buffer.writeShort(widget.optionAttributes)
            }

            if (widget.optionType == Widget.OPTION_OK || widget.optionType == Widget.OPTION_TOGGLE_SETTING || widget.optionType == Widget.OPTION_RESET_SETTING || widget.optionType == Widget.OPTION_CONTINUE) {
                if ((widget.hover == "Ok" && widget.optionType == Widget.OPTION_OK)
                        || (widget.hover == "Select" && (widget.optionType == Widget.OPTION_TOGGLE_SETTING || widget.optionType == Widget.OPTION_RESET_SETTING))
                        || (widget.hover == "Continue" && widget.optionType == Widget.OPTION_CONTINUE))
                    buffer.writeString("")
                else
                    buffer.writeString(widget.hover)
            }
        }

        return buffer
    }

    class Buffer {
        var payload = arrayListOf<Byte>()

        fun writeByte(i: Int) {
            payload.add(i.toByte())
        }

        fun writeByte(i: Byte) {
            payload.add(i)
        }

        fun writeInt(i: Int) {
            payload.add((i shr 24).toByte())
            payload.add((i shr 16).toByte())
            payload.add((i shr 8).toByte())
            payload.add(i.toByte())
        }

        fun writeShort(i: Int) {
            payload.add((i shr 8).toByte())
            payload.add(i.toByte())
        }

        fun writeString(string: String) {
            string.forEach { writeByte(it.toByte()) }
            writeByte(10)
        }
    }
}

fun main(args: Array<String>) {
    val test = SaveTest()


    val archiveFile = test.cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)
    var archive = Archive.decode(archiveFile)


    val dataFile = archive.readFile("data")

    test.load(dataFile)
    val buffer = test.write()

    println("Archive file ${archiveFile.array().size}")
    println("Data file ${dataFile.array().size}")
    println("Buffer file ${buffer.payload.size}")

    println("Replace ${archive.writeFile("data", buffer.payload.toByteArray())}")

    val encoded = archive.encode()

    println("Write ${test.cache.writeFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE, encoded)}")
}