package com.greg.model.cache.archives.font

import com.greg.controller.utils.ColourUtils.toRgba
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.graphics.render.Raster
import java.awt.image.BufferedImage
import java.io.IOException
import kotlin.experimental.and

open class Font : Raster() {
    private var glyphHeights = IntArray(256)
    private var glyphs = arrayOfNulls<ByteArray>(256)
    private var glyphSpacings = IntArray(256)
    private var glyphWidths = IntArray(256)
    private var horizontalOffsets = IntArray(256)
    private var verticalOffsets = IntArray(256)
    private var isStrikeThrough: Boolean = false
    private var verticalSpace: Int = 0

    private fun getColouredTextWidth(text: String?): Int {
        if (text == null) {
            return 0
        } else {
            var width = 0

            var index = 0
            while (index < text.length) {
                if (text[index] == '@' && index + 4 < text.length && text[index + 4] == '@') {
                    index += 4
                } else {
                    width += this.glyphSpacings[text[index].toInt()]
                }
                ++index
            }

            return width
        }
    }

    fun getTextWidth(text: String?): Int {
        return if (text == null) {
            0
        } else {
            var width = 0

            for (index in 0 until text.length) {
                width += this.glyphSpacings[text[index].toInt()]
            }

            width
        }
    }

    fun getLineHeight(text: String?): Int {
        if (text == null)
            return 0

        var height = 0
        for (index in 0 until text.length) {
            val h = glyphHeights[text[index].toInt()] + verticalSpace / 2
            if (h > height)
                height = h
        }

        return height
    }

    fun getAsImage(text: String, shadow: Boolean, centred: Boolean, colour: Int, alpha: Int = 255): BufferedImage? {
        return if (text.contains("\n")) {
            val lines = text.split("\n")
            var maxWidth = 0
            var maxHeight = 0
            lines.forEach {
                val width = getTextWidth(it)
                val height = getLineHeight(it)
                maxHeight += height
                if(width > maxWidth)
                    maxWidth = width
            }
            getImage(lines.toTypedArray(), maxWidth, maxHeight, shadow, centred, toRgba(colour, alpha))
        } else {
            getImage(arrayOf(text), getTextWidth(text), getLineHeight(text), shadow, centred, toRgba(colour, alpha))
        }
    }

    private fun getImage(text: Array<String>, maxWidth: Int, maxHeight: Int, shadow: Boolean, centred: Boolean, rgba: Int): BufferedImage? {
        if (text.isEmpty())
            return null

        val separator = 0

        val size = Math.max(Math.max(maxWidth + if(shadow) 1 else 0, maxHeight + if(shadow) 1 else 0), verticalSpace)

        val pixels = IntArray(size * size)
        Raster.init(size, size, pixels)//Raster's have to be square

        text.forEachIndexed { index, s ->
            if (centred) {
                if (shadow)
                    shadow(maxWidth/2 - getTextWidth(s)/2, verticalSpace + (index * verticalSpace + separator), s, true, rgba)
                else
                    render(s, maxWidth/2 - getTextWidth(s)/2, verticalSpace + (index * verticalSpace + separator), rgba)
            } else {
                if (shadow)
                    shadow(0, verticalSpace + (index * verticalSpace + separator), s, true, rgba)
                else
                    render(s, 0, verticalSpace + (index * verticalSpace + separator), rgba)
            }
        }

        val image = toImage(pixels, size, maxWidth, maxHeight)

        Raster.reset()

        return image
    }

    private fun toImage(pixels: IntArray, size: Int, width: Int, height: Int): BufferedImage {
        val bi = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        bi.setRGB(0, 0, size, size, pixels, 0, size)

        return bi.getSubimage(0, 0, if (width > bi.width) bi.width else width, if (height > bi.height) bi.height else height)
    }

    private fun render(text: String?, x: Int, y: Int, colour: Int) {
        var x = x
        var y = y
        if (text != null) {
            y -= this.verticalSpace

            for (index in 0 until text.length) {
                val character = text[index]
                if (character != ' ') {
                    if (character != ' ' && (character == 'I') or (character == 'i')) {
                    }

                    this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()], this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], toRgba(colour))
                }

                x += this.glyphSpacings[character.toInt()]
            }
        }
    }

    private fun rgb(colour: String): Int {
        when (colour) {
            "red" -> return 16711680
            "gre" -> return 65280
            "blu" -> return 255
            "yel" -> return 16776960
            "cya" -> return 65535
            "mag" -> return 16711935
            "whi" -> return 16777215
            "bla" -> return 0
            "lre" -> return 16748608
            "dre" -> return 8388608
            "dbl" -> return 128
            "or1" -> return 16756736
            "or2" -> return 16740352
            "or3" -> return 16723968
            "gr1" -> return 12648192
            "gr2" -> return 8453888
            "gr3" -> return 4259584
            else -> {
                if (colour == "str") {
                    this.isStrikeThrough = true
                } else if (colour == "end") {
                    this.isStrikeThrough = false
                }

                return -1
            }
        }
    }

    private fun shadow(x: Int, y: Int, text: String?, shadow: Boolean, colour: Int) {
        var x = x
        var y = y
        var colour = colour
        this.isStrikeThrough = false
        val width = x
        if (text != null) {
            y -= this.verticalSpace

            var index = 0
            while (index < text.length) {
                if (text[index] == '@' && index + 4 < text.length && text[index + 4] == '@') {
                    val rgb = this.rgb(text.substring(index + 1, index + 4))
                    if (rgb != -1) {
                        colour = rgb
                    }

                    index += 4
                } else {
                    val character = text[index]
                    if (character != ' ') {
                        if (shadow) {
                            this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()] + 1, y + this.verticalOffsets[character.toInt()] + 1, this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], toRgba(0))
                        }

                        this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()], this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], toRgba(colour))
                    }

                    x += this.glyphSpacings[character.toInt()]
                }
                ++index
            }

            if (this.isStrikeThrough) {
                Raster.drawHorizontal(x, y + (this.verticalSpace.toDouble() * 0.7).toInt(), x - width, 8388608)
            }
        }
    }

    private fun render(glyph: ByteArray, x: Int, y: Int, width: Int, height: Int, colour: Int) {
        var x = x
        var y = y
        var width = width
        var height = height
        var rasterIndex = x + y * Raster.width
        var rasterClip = Raster.width - width
        var glyphClip = 0
        var glyphIndex = 0
        var dx: Int
        if (y < Raster.clipBottom) {
            dx = Raster.clipBottom - y
            height -= dx
            y = Raster.clipBottom
            glyphIndex += dx * width
            rasterIndex += dx * Raster.width
        }

        if (y + height >= Raster.clipTop) {
            height -= y + height - Raster.clipTop + 1
        }

        if (x < Raster.clipLeft) {
            dx = Raster.clipLeft - x
            width -= dx
            x = Raster.clipLeft
            glyphIndex += dx
            rasterIndex += dx
            glyphClip += dx
            rasterClip += dx
        }

        if (x + width >= Raster.clipRight) {
            dx = x + width - Raster.clipRight + 1
            width -= dx
            glyphClip += dx
            rasterClip += dx
        }

        if (width > 0 && height > 0) {
            this.render(Raster.raster, glyph, colour, glyphIndex, rasterIndex, width, height, rasterClip, glyphClip)
        }

    }

    private fun render(raster: IntArray, glyph: ByteArray, colour: Int, glyphPosition: Int, rasterPosition: Int, width: Int, height: Int, rasterOffset: Int, glyphOffset: Int) {
        var glyphPosition = glyphPosition
        var rasterPosition = rasterPosition
        var width = width
        val offsetX = -(width shr 2)
        width = -(width and 3)

        for (y in -height..-1) {
            var i: Int
            i = offsetX
            while (i < 0) {
                if (glyph[glyphPosition++].toInt() != 0) {
                    raster[rasterPosition++] = colour
                } else {
                    ++rasterPosition
                }

                if (glyph[glyphPosition++].toInt() != 0) {
                    raster[rasterPosition++] = colour
                } else {
                    ++rasterPosition
                }

                if (glyph[glyphPosition++].toInt() != 0) {
                    raster[rasterPosition++] = colour
                } else {
                    ++rasterPosition
                }

                if (glyph[glyphPosition++].toInt() != 0) {
                    raster[rasterPosition++] = colour
                } else {
                    ++rasterPosition
                }
                ++i
            }

            i = width
            while (i < 0) {
                if (glyph[glyphPosition++].toInt() != 0) {
                    raster[rasterPosition++] = colour
                } else {
                    ++rasterPosition
                }
                ++i
            }

            rasterPosition += rasterOffset
            glyphPosition += glyphOffset
        }

    }

    companion object {

        @Throws(IOException::class)
        fun decode(archive: Archive, name: String, wideSpace: Boolean): Font {
            val font = Font()
            val data = archive.readFile("$name.dat")
            val meta = archive.readFile("index.dat")
            meta.position((data.short and '\uffff'.toShort()) + 4)
            val position = meta.get() and 255.toByte()
            if (position > 0) {
                meta.position(meta.position() + 3 * (position - 1))
            }

            for (character in 0..255) {
                font.horizontalOffsets[character] = meta.get().toInt() and 255
                font.verticalOffsets[character] = meta.get().toInt() and 255
                font.glyphWidths[character] = meta.short.toInt() and 0xffff
                val width = font.glyphWidths[character]
                font.glyphHeights[character] = meta.short.toInt() and 0xffff
                val height = font.glyphHeights[character]
                val format = meta.get() and 255.toByte()
                val pixels = width * height
                font.glyphs[character] = ByteArray(pixels)
                var filledCount: Int
                var y: Int
                if (format == 0.toByte()) {
                    filledCount = 0
                    while (filledCount < pixels) {
                        font.glyphs[character]!![filledCount] = data.get()
                        filledCount++
                    }
                } else if (format == 1.toByte()) {
                    filledCount = 0
                    while (filledCount < width) {
                        y = 0
                        while (y < height) {
                            font.glyphs[character]!![filledCount + y * width] = data.get()
                            y++
                        }
                        filledCount++
                    }
                }

                if (height > font.verticalSpace && character < 128) {
                    font.verticalSpace = height
                }

                font.horizontalOffsets[character] = 1
                font.glyphSpacings[character] = width + 2
                filledCount = 0

                y = height / 7
                while (y < height) {
                    filledCount += font.glyphs[character]!![y * width].toInt()
                    y++
                }

                if (filledCount <= height / 7) {
                    --font.glyphSpacings[character]
                    font.horizontalOffsets[character] = 0
                }

                filledCount = 0

                y = height / 7
                while (y < height) {
                    filledCount += font.glyphs[character]!![width - 1 + y * width].toInt()
                    y++
                }

                if (filledCount <= height / 7) {
                    font.glyphSpacings[character]--
                }
            }

            font.glyphSpacings[32] = if (wideSpace) font.glyphSpacings[73] else font.glyphSpacings[105]
            return font
        }
    }
}
