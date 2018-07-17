package io.nshusa.rsam.binary

import io.nshusa.rsam.graphics.render.Raster
import java.io.IOException
import java.util.*
import kotlin.experimental.and

class Font private constructor() : Raster() {
    var glyphHeights = IntArray(256)
    var glyphs = arrayOfNulls<ByteArray>(256)
    var glyphSpacings = IntArray(256)
    var glyphWidths = IntArray(256)
    var horizontalOffsets = IntArray(256)
    var verticalOffsets = IntArray(256)
    var random = Random()
    private var isStrikeThrough: Boolean = false
    var verticalSpace: Int = 0

    fun getColouredTextWidth(text: String?): Int {
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
        if (text == null) {
            return 0
        } else {
            var width = 0

            for (index in 0 until text.length) {
                width += this.glyphSpacings[text[index].toInt()]
            }

            return width
        }
    }

    fun render(text: String?, x: Int, y: Int, colour: Int) {
        var x = x
        var y = y
        if (text != null) {
            y -= this.verticalSpace

            for (index in 0 until text.length) {
                val character = text[index]
                if (character != ' ') {
                    if (character != ' ' && (character == 'I') or (character == 'i')) {
                    }

                    this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()], this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], colour)
                }

                x += this.glyphSpacings[character.toInt()]
            }

        }
    }

    fun renderCentre(x: Int, y: Int, text: String, colour: Int) {
        this.render(text, x - this.getTextWidth(text) / 2, y, colour)
    }

    fun renderLeft(x: Int, y: Int, text: String, colour: Int) {
        this.render(text, x - this.getTextWidth(text), y, colour)
    }

    fun renderRandom(text: String?, x: Int, y: Int, colour: Int, shadow: Boolean, seed: Int) {
        var x = x
        var y = y
        var colour = colour
        if (text != null) {
            this.random.setSeed(seed.toLong())
            val alpha = 192 + (this.random.nextInt() and 31)
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
                            this.renderRgba(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()] + 1, y + 1 + this.verticalOffsets[character.toInt()], this.glyphHeights[character.toInt()], this.glyphWidths[character.toInt()], 192, 0)
                        }

                        this.renderRgba(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()], this.glyphHeights[character.toInt()], this.glyphWidths[character.toInt()], alpha, colour)
                    }

                    x += this.glyphSpacings[character.toInt()]
                    if (this.random.nextInt() and 3 == 0) {
                        ++x
                    }
                }
                ++index
            }

        }
    }

    fun rgb(colour: String): Int {
        if (colour == "red") {
            return 16711680
        } else if (colour == "gre") {
            return 65280
        } else if (colour == "blu") {
            return 255
        } else if (colour == "yel") {
            return 16776960
        } else if (colour == "cya") {
            return 65535
        } else if (colour == "mag") {
            return 16711935
        } else if (colour == "whi") {
            return 16777215
        } else if (colour == "bla") {
            return 0
        } else if (colour == "lre") {
            return 16748608
        } else if (colour == "dre") {
            return 8388608
        } else if (colour == "dbl") {
            return 128
        } else if (colour == "or1") {
            return 16756736
        } else if (colour == "or2") {
            return 16740352
        } else if (colour == "or3") {
            return 16723968
        } else if (colour == "gr1") {
            return 12648192
        } else if (colour == "gr2") {
            return 8453888
        } else if (colour == "gr3") {
            return 4259584
        } else {
            if (colour == "str") {
                this.isStrikeThrough = true
            } else if (colour == "end") {
                this.isStrikeThrough = false
            }

            return -1
        }
    }

    fun shadow(x: Int, y: Int, text: String?, shadow: Boolean, colour: Int) {
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
                            this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()] + 1, y + this.verticalOffsets[character.toInt()] + 1, this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], 0)
                        }

                        this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()], this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], colour)
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

    fun shadowCentre(x: Int, y: Int, text: String, shadow: Boolean, colour: Int) {
        this.shadow(x - this.getColouredTextWidth(text) / 2, y, text, shadow, colour)
    }

    fun shake(text: String?, x: Int, y: Int, colour: Int, elapsed: Int, tick: Int) {
        var x = x
        var y = y
        if (text != null) {
            var amplitude = 7.0 - elapsed.toDouble() / 8.0
            if (amplitude < 0.0) {
                amplitude = 0.0
            }

            x -= this.getTextWidth(text) / 2
            y -= this.verticalSpace

            for (index in 0 until text.length) {
                val character = text[index]
                if (character != ' ') {
                    this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()], y + this.verticalOffsets[character.toInt()] + (Math.sin(index.toDouble() / 1.5 + tick.toDouble()) * amplitude).toInt(), this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], colour)
                }

                x += this.glyphSpacings[character.toInt()]
            }

        }
    }

    fun wave(text: String?, x: Int, y: Int, colour: Int, tick: Int) {
        var x = x
        var y = y
        if (text != null) {
            x -= this.getTextWidth(text) / 2
            y -= this.verticalSpace

            for (index in 0 until text.length) {
                val c = text[index]
                if (c != ' ') {
                    this.render(this.glyphs[c.toInt()]!!, x + this.horizontalOffsets[c.toInt()], y + this.verticalOffsets[c.toInt()] + (Math.sin(index.toDouble() / 2.0 + tick.toDouble() / 5.0) * 5.0).toInt(), this.glyphWidths[c.toInt()], this.glyphHeights[c.toInt()], colour)
                }

                x += this.glyphSpacings[c.toInt()]
            }

        }
    }

    fun wave2(text: String?, x: Int, y: Int, colour: Int, tick: Int) {
        var x = x
        var y = y
        if (text != null) {
            x -= this.getTextWidth(text) / 2
            y -= this.verticalSpace

            for (index in 0 until text.length) {
                val character = text[index]
                if (character != ' ') {
                    this.render(this.glyphs[character.toInt()]!!, x + this.horizontalOffsets[character.toInt()] + (Math.sin(index.toDouble() / 5.0 + tick.toDouble() / 5.0) * 5.0).toInt(), y + this.verticalOffsets[character.toInt()] + (Math.sin(index.toDouble() / 3.0 + tick.toDouble() / 5.0) * 5.0).toInt(), this.glyphWidths[character.toInt()], this.glyphHeights[character.toInt()], colour)
                }

                x += this.glyphSpacings[character.toInt()]
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

    private fun renderRgba(glyph: ByteArray, x: Int, y: Int, height: Int, width: Int, alpha: Int, colour: Int) {
        var x = x
        var y = y
        var height = height
        var width = width
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
            this.renderRgba(glyph, height, rasterIndex, Raster.raster, glyphIndex, width, glyphClip, rasterClip, colour, alpha)
        }

    }

    private fun renderRgba(glyph: ByteArray, height: Int, rasterPosition: Int, raster: IntArray, glyphPosition: Int, width: Int, glyphOffset: Int, rasterOffset: Int, colour: Int, alpha: Int) {
        var rasterPosition = rasterPosition
        var glyphPosition = glyphPosition
        var colour = colour
        var alpha = alpha
        colour = ((colour and 16711935) * alpha and -16711936) + ((colour and '\uff00'.toInt()) * alpha and 16711680) shr 8
        alpha = 256 - alpha

        for (y in -height..-1) {
            for (x in -width..-1) {
                if (glyph[glyphPosition++].toInt() != 0) {
                    val rgba = raster[rasterPosition]
                    raster[rasterPosition++] = (((rgba and 16711935) * alpha and -16711936) + ((rgba and '\uff00'.toInt()) * alpha and 16711680) shr 8) + colour
                } else {
                    ++rasterPosition
                }
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
                font.glyphWidths[character] = meta.short.toInt() and '\uffff'.toInt()
                val width = font.glyphWidths[character]
                font.glyphHeights[character] = meta.short.toInt() and '\uffff'.toInt()
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
                        ++filledCount
                    }
                } else if (format == 1.toByte()) {
                    filledCount = 0
                    while (filledCount < width) {
                        y = 0
                        while (y < height) {
                            font.glyphs[character]!![filledCount + y * width] = data.get()
                            ++y
                        }
                        ++filledCount
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
                    ++y
                }

                if (filledCount <= height / 7) {
                    --font.glyphSpacings[character]
                    font.horizontalOffsets[character] = 0
                }

                filledCount = 0

                y = height / 7
                while (y < height) {
                    filledCount += font.glyphs[character]!![width - 1 + y * width].toInt()
                    ++y
                }

                if (filledCount <= height / 7) {
                    --font.glyphSpacings[character]
                }
            }

            font.glyphSpacings[32] = if (wideSpace) font.glyphSpacings[73] else font.glyphSpacings[105]
            return font
        }
    }
}
