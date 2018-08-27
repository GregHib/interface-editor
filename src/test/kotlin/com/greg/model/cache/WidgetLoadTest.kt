package com.greg.model.cache

import com.greg.controller.utils.ColourUtils
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.*
import com.greg.model.widgets.type.groups.GroupAppearance
import com.greg.model.widgets.type.groups.GroupColour
import com.greg.model.widgets.type.groups.GroupColours
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.util.ByteBufferUtils
import kotlin.experimental.and

class WidgetLoadTest {
    val cache = Cache(CachePath("./cache/"))
    val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

    var widgets: Array<Widget?>? = null

    fun load(): Boolean {
        return try {
            val buffer = archive.readFile("data")

            widgets = arrayOfNulls(buffer.short.toInt() and 0xffff)

            var parent = -1

            var widget: Widget
            while (buffer.hasRemaining()) {
                var id = buffer.short.toInt() and 0xffff
                if (id == 65535) {
                    parent = buffer.short.toInt() and 0xffff
                    id = buffer.short.toInt() and 0xffff
                }

                val group = buffer.get().toInt() and 255

                widget = WidgetBuilder(WidgetType.values()[group]).build(id)
//                widget.setParent(parent)

                widget.setOptionType(buffer.get().toInt() and 255)
                widget.setContentType(buffer.short.toInt() and 0xffff)
                widget.setWidth(buffer.short.toInt() and 0xffff)
                widget.setHeight(buffer.short.toInt() and 0xffff)
                widget.setAlpha((buffer.get() and 255.toByte()).toInt())
                val hoverId = buffer.get() and 255.toByte()
                val hoverPart2 = if (hoverId != 0.toByte()) buffer.get().toInt() else -1

                widget.setHoverId(if (hoverId != 0.toByte()) hoverId - 1 shl 8 or (hoverPart2 and 255) else -1)

                val operators = buffer.get().toInt() and 255
                if (operators > 0) {
                    val scriptOperators = IntArray(operators)
                    val scriptDefaults = IntArray(operators)

                    var scripts = 0
                    while (scripts < operators) {
                        scriptOperators[scripts] = buffer.get().toInt() and 255
                        scriptDefaults[scripts] = buffer.short.toInt() and 0xffff
                        scripts++
                    }

                    widget.setScriptOperators(scriptOperators)
                    widget.setScriptDefaults(scriptDefaults)
                }

                val scriptCount = buffer.get().toInt() and 255
                if (scriptCount > 0) {
                    val scripts = arrayOfNulls<IntArray>(scriptCount)

                    var index = 0
                    while (index < scriptCount) {
                        val length = buffer.short.toInt() and 0xffff
                        scripts[index] = IntArray(length)

                        for (instruction in 0 until length) {
                            scripts[index]!![instruction] = buffer.short.toInt() and 0xffff
                        }
                        index++
                    }
                }

                if(widget is WidgetContainer) {
                    widget.setScrollLimit(buffer.short.toInt() and 0xffff)
                    widget.setHidden(buffer.get().toInt() and 255 == 1)
                    val amount = buffer.short.toInt() and 0xffff
                    val children = IntArray(amount)
                    val childX = IntArray(amount)
                    val childY = IntArray(amount)

                    var index = 0
                    while (index < amount) {
                        children[index] = buffer.short.toInt() and 0xffff
                        childX[index] = buffer.short.toInt()
                        childY[index] = buffer.short.toInt()
                        index++
                    }

                    //widget.setChildren()
                }
                if (widget is WidgetModelList) {
                    buffer.short
                    buffer.get()
                }

                if (widget is WidgetInventory) {
//                    widget.inventoryIds = IntArray(widget.width * widget.height)
//                    widget.inventoryAmounts = IntArray(widget.width * widget.height)
                    widget.setSwappableItems(buffer.get().toInt() and 255 == 1)
                    widget.setHasActions(buffer.get().toInt() and 255 == 1)
                    widget.setUsableItems(buffer.get().toInt() and 255 == 1)
                    widget.setReplaceItems(buffer.get().toInt() and 255 == 1)
                    widget.setSpritePaddingX(buffer.get().toInt() and 255)
                    widget.setSpritePaddingY(buffer.get().toInt() and 255)
                    val spriteX = IntArray(20)
                    val spriteY = IntArray(20)
                    val sprites = arrayOfNulls<String>(20)
                    val spritesArchive = arrayOfNulls<String>(20)
                    val spritesIndex = arrayOfNulls<Int>(20)

                    var counter = 0
                    while (counter < 20) {
                        val index = buffer.get().toInt() and 255
                        if (index == 1) {
                            spriteX[counter] = buffer.short.toInt()
                            spriteY[counter] = buffer.short.toInt()
                            val name = ByteBufferUtils.getString(buffer)
                            sprites[counter] = name
                            if (name.isNotEmpty()) {
                                val position = name.lastIndexOf(",")
                                spritesArchive[counter] = name.substring(0, position)
                                spritesIndex[counter] = Integer.parseInt(name.substring(position + 1))
                            }
                        }
                        counter++
                    }


                    val actions = arrayOfNulls<String>(5)

                    var index = 0
                    while (index < 5) {
                        actions[index] = ByteBufferUtils.getString(buffer)
                        if (actions[index]!!.isEmpty()) {
                            actions[index] = null
                        }
                        index++
                    }
                    widget.setActions(actions)
                }

                if (widget is WidgetRectangle) {
                    widget.setFilled(buffer.get().toInt() and 255 == 1)
                }

                if ((widget is WidgetText || widget is WidgetModelList) && widget is GroupAppearance) {
                    widget.setCentred(buffer.get().toInt() and 255 == 1)
                    widget.setFontIndex(buffer.get().toInt() and 255)

                    widget.setShadow(buffer.get().toInt() and 255 == 1)
                }

                if (widget is WidgetText) {
                    widget.setDefaultText(ByteBufferUtils.getString(buffer))
                    widget.setSecondaryText(ByteBufferUtils.getString(buffer))
                }

                if ((widget is WidgetModelList || widget is WidgetRectangle || widget is WidgetText)&& widget is GroupColour) {
                    widget.setDefaultColour(ColourUtils.getColour(buffer.int))
                }

                if ((widget is WidgetRectangle || widget is WidgetText) && widget is GroupColours) {
                    widget.setSecondaryColour(ColourUtils.getColour(buffer.int))
                    widget.setDefaultHoverColour(ColourUtils.getColour(buffer.int))
                    widget.setSecondaryHoverColour(ColourUtils.getColour(buffer.int))
                }

                if (widget is WidgetSprite) {
                    var name = ByteBufferUtils.getString(buffer)


                    if (name.isNotEmpty()) {
                        val index = name.lastIndexOf(",")
                        widget.setDefaultSpriteArchive(name.substring(0, index))
                        widget.setDefaultSprite(Integer.parseInt(name.substring(index + 1)), false)
                    }

                    name = ByteBufferUtils.getString(buffer)
                    if (name.isNotEmpty()) {
                        val index = name.lastIndexOf(",")
                        widget.setSecondarySpriteArchive(name.substring(0, index))
                        widget.setSecondarySprite(Integer.parseInt(name.substring(index + 1)), false)
                    }
                }

                if (widget is WidgetModel) {
                    var value = buffer.get().toInt() and 255
                    if (value != 0) {
                        widget.setDefaultMediaType(1)
                        widget.setDefaultMedia((value - 1 shl 8) + (buffer.get().toInt() and 255))
                    }

                    value = buffer.get().toInt() and 255
                    if (value != 0) {
                        widget.setSecondaryMediaType(1)
                        widget.setSecondaryMedia((value - 1 shl 8) + (buffer.get().toInt() and 255))
                    }

                    value = buffer.get().toInt() and 255
                    widget.setDefaultAnimationId(if (value != 0) (value - 1 shl 8) + (buffer.get().toInt() and 255) else -1)
                    value = buffer.get().toInt() and 255
                    widget.setSecondaryAnimationId(if (value != 0) (value - 1 shl 8) + (buffer.get().toInt() and 255) else -1)
                    widget.setSpriteScale(buffer.short.toInt() and 0xffff)
                    widget.setSpritePitch(buffer.short.toInt() and 0xffff)
                    widget.setSpriteRoll(buffer.short.toInt() and 0xffff)
                }

                if (widget is WidgetItemList) {
//                    widget.inventoryIds = IntArray(widget.width * widget.height)
//                    widget.inventoryAmounts = IntArray(widget.width * widget.height)
                    widget.setCentred(buffer.get().toInt() and 255 == 1)
                    widget.setFontIndex(buffer.get().toInt() and 255)

                    widget.setShadow(buffer.get().toInt() and 255 == 1)
                    widget.setDefaultColour(ColourUtils.getColour(buffer.int))
                    widget.setSpritePaddingX(buffer.short.toInt())
                    widget.setSpritePaddingY(buffer.short.toInt())
                    widget.setHasActions(buffer.get().toInt() and 255 == 1)
                    val actions = arrayOfNulls<String>(5)

                    var index = 0
                    while (index < 5) {
                        actions[index] = ByteBufferUtils.getString(buffer)
                        if (actions[index]!!.isEmpty()) {
                            actions[index] = null
                        }
                        index++
                    }
                    widget.setActions(actions)
                }

                if (widget.getOptionType() == WidgetData.OPTION_USABLE || widget is WidgetInventory) {
                    widget.setOptionCircumfix(ByteBufferUtils.getString(buffer))
                    widget.setOptionText(ByteBufferUtils.getString(buffer))
                    widget.setOptionAttributes(buffer.short.toInt() and 0xffff)
                }

                if (widget.getOptionType() == WidgetData.OPTION_OK || widget.getOptionType() == WidgetData.OPTION_TOGGLE_SETTING || widget.getOptionType() == WidgetData.OPTION_RESET_SETTING || widget.getOptionType() == WidgetData.OPTION_CONTINUE) {
                    var hover = ByteBufferUtils.getString(buffer)

                    if (hover.isEmpty()) {
                        hover = when (widget.getOptionType()) {
                            WidgetData.OPTION_OK -> "Ok"
                            WidgetData.OPTION_TOGGLE_SETTING -> "Select"
                            WidgetData.OPTION_RESET_SETTING -> "Select"
                            WidgetData.OPTION_CONTINUE -> "Continue"
                            else -> hover
                        }
                    }
                    widget.setHover(hover)
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

                widgets!![id] = widget
            }

            true
        } catch (e: NullPointerException) {
            e.printStackTrace()
            cache.reset()
            false
        }
    }
}

fun main(args: Array<String>) {
    val load = WidgetLoadTest()
    val save = SaveTest()

    var start = System.currentTimeMillis()
    load.load()
    println("Load complete in ${System.currentTimeMillis() - start}")

    start = System.currentTimeMillis()
    save.load()
    println("Load complete in ${System.currentTimeMillis() - start}")

    println(load.widgets!!.size)

//    FileUtils.writeByteArrayToFile(File("interface.jag"), test.archive.encode())

}