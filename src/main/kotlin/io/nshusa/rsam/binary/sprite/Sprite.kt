package io.nshusa.rsam.binary.sprite

import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.graphics.render.Raster
import io.nshusa.rsam.util.ByteBufferUtils
import io.nshusa.rsam.util.HashUtils
import java.awt.Color
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.awt.image.FilteredImageSource
import java.awt.image.RGBImageFilter
import java.io.IOException
import java.nio.ByteBuffer





class Sprite {

    var id: Int = 0
        private set
    var archive: Int = 0
        private set
    var width: Int = 0
    var height: Int = 0
    var offsetX: Int = 0
    var offsetY: Int = 0
    var resizeWidth: Int = 0
    var resizeHeight: Int = 0
    var pixels: IntArray? = null
    var format: Int = 0

    constructor()

    constructor(width: Int, height: Int) {
        this.pixels = IntArray(width * height)
        this.resizeWidth = width
        this.width = this.resizeWidth
        this.resizeHeight = height
        this.height = this.resizeHeight
    }

    constructor(resizeWidth: Int, resizeHeight: Int, horizontalOffset: Int, verticalOffset: Int, width: Int, height: Int, format: Int, pixels: IntArray) {
        this.resizeWidth = resizeWidth
        this.resizeHeight = resizeHeight
        this.offsetX = horizontalOffset
        this.offsetY = verticalOffset
        this.width = width
        this.height = height
        this.format = format
        this.pixels = pixels
    }

    fun drawSprite(x: Int, y: Int) {
        var x = x
        var y = y
        x += offsetX
        y += offsetY
        var rasterClip = x + y * Raster.width
        var imageClip = 0
        var height = this.height
        var width = this.width
        var rasterOffset = Raster.width - width
        var imageOffset = 0

        if (y < Raster.clipBottom) {
            val dy = Raster.clipBottom - y
            height -= dy
            y = Raster.clipBottom
            imageClip += dy * width
            rasterClip += dy * Raster.width
        }

        if (y + height > Raster.clipTop) {
            height -= y + height - Raster.clipTop
        }

        if (x < Raster.clipLeft) {
            val dx = Raster.clipLeft - x
            width -= dx
            x = Raster.clipLeft
            imageClip += dx
            rasterClip += dx
            imageOffset += dx
            rasterOffset += dx
        }

        if (x + width > Raster.clipRight) {
            val dx = x + width - Raster.clipRight
            width -= dx
            imageOffset += dx
            rasterOffset += dx
        }

        if (width > 0 && height > 0) {
            draw(Raster.raster, pixels, 0, imageClip, rasterClip, width, height, rasterOffset, imageOffset)
        }
    }

    private fun draw(raster: IntArray, image: IntArray?, colour: Int, sourceIndex: Int, destIndex: Int, width: Int, height: Int, destStep: Int,
                     sourceStep: Int) {
        var colour = colour
        var sourceIndex = sourceIndex
        var destIndex = destIndex
        var width = width
        val minX = -(width shr 2)
        width = -(width and 3)

        for (y in -height..-1) {
            for (x in minX..-1) {
                colour = image!![sourceIndex++]
                if (colour != 0) {
                    raster[destIndex++] = colour
                } else {
                    destIndex++
                }
                colour = image[sourceIndex++]

                if (colour != 0) {
                    raster[destIndex++] = colour
                } else {
                    destIndex++
                }
                colour = image[sourceIndex++]

                if (colour != 0) {
                    raster[destIndex++] = colour
                } else {
                    destIndex++
                }
                colour = image[sourceIndex++]

                if (colour != 0) {
                    raster[destIndex++] = colour
                } else {
                    destIndex++
                }
            }

            for (k2 in width..-1) {
                colour = image!![sourceIndex++]
                if (colour != 0) {
                    raster[destIndex++] = colour
                } else {
                    destIndex++
                }
            }

            destIndex += destStep
            sourceIndex += sourceStep
        }
    }

    private fun makeColorTransparent(im: BufferedImage, color: Color): Image {
        val filter = object : RGBImageFilter() {
            var markerRGB = color.rgb or -0x1000000
            override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
                return if (rgb or -0x1000000 == markerRGB)
                    0x00FFFFFF and rgb
                else
                    rgb
            }
        }
        val ip = FilteredImageSource(im.source, filter)
        return Toolkit.getDefaultToolkit().createImage(ip)
    }

    private fun imageToBufferedImage(image: Image): BufferedImage {
        val bufferedImage = BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        val g2 = bufferedImage.createGraphics()
        g2.drawImage(image, 0, 0, null)
        g2.dispose()
        return bufferedImage
    }

    fun toBufferedImage(): BufferedImage {
        val bi = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        bi.setRGB(0, 0, width, height, pixels!!, 0, width)
        val img = makeColorTransparent(bi, Color(0, 0, 0))

        return imageToBufferedImage(img)
    }

    companion object {
        @Throws(IOException::class)
        fun decode(archive: Archive, metaBuf: ByteBuffer = archive.readFile("index.dat"), hash: Int, id: Int): Sprite {
            val dataBuf = archive.readFile(hash)

            val sprite = Sprite()
            sprite.id = id
            sprite.archive = hash

            // position of the current image archive within the archive
            metaBuf.position(dataBuf.short.toInt() and 0xFFFF)

            // the maximum width the images in this archive can scale to
            sprite.resizeWidth = metaBuf.short.toInt() and 0xFFFF

            // the maximum height the images in this archive can scale to
            sprite.resizeHeight = metaBuf.short.toInt() and 0xFFFF

            // the number of colors that are used in this image archive (limit is 256 if one of the rgb values is 0 else its 255)
            val colours = metaBuf.get().toInt() and 0xFF

            // the array of colors that can only be used in this archive
            val palette = IntArray(colours)

            for (index in 0 until colours - 1) {
                val colour = ByteBufferUtils.readU24Int(metaBuf)
                // + 1 because index = 0 is for transparency, = 1 is a flag for opacity. (BufferedImage#OPAQUE)
                palette[index + 1] = if (colour == 0) 1 else colour
            }

            for (i in 0 until id) {
                // skip the current offsetX and offsetY
                metaBuf.position(metaBuf.position() + 2)

                // skip the current array of pixels
                dataBuf.position(dataBuf.position() + (metaBuf.short.toInt() and 0xFFFF) * (metaBuf.short.toInt() and 0xFFFF))

                // skip the current format
                metaBuf.position(metaBuf.position() + 1)
            }

            // offsets are used to reposition the sprite on an interface.
            sprite.offsetX = metaBuf.get().toInt() and 0xFF
            sprite.offsetY = metaBuf.get().toInt() and 0xFF

            // actual width of this sprite
            sprite.width = metaBuf.short.toInt() and 0xFFFF

            // actual height of this sprite
            sprite.height = metaBuf.short.toInt() and 0xFFFF

            // there are 2 ways the pixels can be written (0 or 1, 0 means the position is read horizontally, 1 means vertically)
            sprite.format = metaBuf.get().toInt() and 0xFF

            if (sprite.format != 0 && sprite.format != 1) {
                throw IOException(String.format("Detected end of archive=%d id=%d or wrong format=%d", hash, id, sprite.format))
            }

            if (sprite.width > 765 || sprite.height > 765 || sprite.width <= 0 || sprite.height <= 0) {
                throw IOException(String.format("Detected end of archive=%d id=%d", hash, id))
            }

            val raster = IntArray(sprite.width * sprite.height)

            if (sprite.format == 0) { // read horizontally
                for (index in raster.indices) {
                    raster[index] = palette[dataBuf.get().toInt() and 0xFF]
                }
            } else if (sprite.format == 1) { // read vertically
                for (x in 0 until sprite.width) {
                    for (y in 0 until sprite.height) {
                        raster[x + y * sprite.width] = palette[dataBuf.get().toInt() and 0xFF]
                    }
                }
            }
            sprite.pixels = raster
            return sprite
        }


        @Throws(IOException::class)
        fun decode(archive: Archive, name: String, id: Int): Sprite {
            return decode(archive, hash = HashUtils.nameToHash(if (name.contains(".dat")) name else "$name.dat"), id = id)
        }
    }

}