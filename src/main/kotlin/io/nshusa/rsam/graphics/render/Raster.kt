package io.nshusa.rsam.graphics.render

open class Raster {
    companion object {

        var maxRight: Int = 0
        var height: Int = 0
        lateinit var raster: IntArray
        var width: Int = 0
        var centreX: Int = 0
        var centreY: Int = 0
        var clipBottom: Int = 0
        var clipLeft: Int = 0
        var clipRight: Int = 0
        var clipTop: Int = 0

        fun drawHorizontal(x: Int, y: Int, length: Int, colour: Int) {
            var x = x
            var length = length
            if (y < clipBottom || y >= clipTop) {
                return
            }

            if (x < clipLeft) {
                length -= clipLeft - x
                x = clipLeft
            }

            if (x + length > clipRight) {
                length = clipRight - x
            }

            val offset = x + y * width
            for (index in 0 until length) {
                raster[offset + index] = colour
            }
        }

        fun drawHorizontal(x: Int, y: Int, length: Int, colour: Int, alpha: Int) {
            var x = x
            var length = length
            if (y < clipBottom || y >= clipTop) {
                return
            }

            if (x < clipLeft) {
                length -= clipLeft - x
                x = clipLeft
            }

            if (x + length > clipRight) {
                length = clipRight - x
            }

            val invertedAlpha = 256 - alpha
            val r = (colour shr 16 and 0xff) * alpha
            val g = (colour shr 8 and 0xff) * alpha
            val b = (colour and 0xff) * alpha
            var index = x + y * width

            for (i in 0 until length) {
                val r2 = (raster[index] shr 16 and 0xff) * invertedAlpha
                val g2 = (raster[index] shr 8 and 0xff) * invertedAlpha
                val b2 = (raster[index] and 0xff) * invertedAlpha
                raster[index++] = (r + r2 shr 8 shl 16) + (g + g2 shr 8 shl 8) + (b + b2 shr 8)
            }
        }

        fun drawRectangle(x: Int, y: Int, width: Int, height: Int, colour: Int) {
            drawHorizontal(x, y, width, colour)
            drawHorizontal(x, y + height - 1, width, colour)
            drawVertical(x, y, height, colour)
            drawVertical(x + width - 1, y, height, colour)
        }

        fun drawRectangle(x: Int, y: Int, width: Int, height: Int, colour: Int, alpha: Int) {
            drawHorizontal(x, y, width, colour, alpha)
            drawHorizontal(x, y + height - 1, width, colour, alpha)
            if (height >= 3) {
                drawVertical(x, y + 1, height - 2, colour, alpha)
                drawVertical(x + width - 1, y + 1, height - 2, colour, alpha)
            }
        }

        fun drawVertical(x: Int, y: Int, length: Int, colour: Int) {
            var y = y
            var length = length
            if (x < clipLeft || x >= clipRight) {
                return
            }

            if (y < clipBottom) {
                length -= clipBottom - y
                y = clipBottom
            }

            if (y + length > clipTop) {
                length = clipTop - y
            }

            val offset = x + y * width
            for (index in 0 until length) {
                raster[offset + index * width] = colour
            }
        }

        fun drawVertical(x: Int, y: Int, length: Int, colour: Int, alpha: Int) {
            var y = y
            var length = length
            if (x < clipLeft || x >= clipRight) {
                return
            }

            if (y < clipBottom) {
                length -= clipBottom - y
                y = clipBottom
            }

            if (y + length > clipTop) {
                length = clipTop - y
            }

            val invertedAlpha = 256 - alpha
            val r = (colour shr 16 and 0xff) * alpha
            val g = (colour shr 8 and 0xff) * alpha
            val b = (colour and 0xff) * alpha
            var index = x + y * width

            for (i in 0 until length) {
                val r2 = (raster[index] shr 16 and 0xff) * invertedAlpha
                val g2 = (raster[index] shr 8 and 0xff) * invertedAlpha
                val b2 = (raster[index] and 0xff) * invertedAlpha
                raster[index] = (r + r2 shr 8 shl 16) + (g + g2 shr 8 shl 8) + (b + b2 shr 8)
                index += width
            }
        }

        fun fillRectangle(x: Int, y: Int, width: Int, height: Int, colour: Int) {
            var x = x
            var y = y
            var width = width
            var height = height
            if (x < clipLeft) {
                width -= clipLeft - x
                x = clipLeft
            }

            if (y < clipBottom) {
                height -= clipBottom - y
                y = clipBottom
            }

            if (x + width > clipRight) {
                width = clipRight - x
            }

            if (y + height > clipTop) {
                height = clipTop - y
            }

            val dx = Raster.width - width
            var pixel = x + y * Raster.width

            for (i2 in -height..-1) {
                for (j2 in -width..-1) {
                    raster[pixel++] = colour
                }

                pixel += dx
            }
        }

        fun fillRectangle(drawX: Int, drawY: Int, width: Int, height: Int, colour: Int, alpha: Int) {
            var drawX = drawX
            var drawY = drawY
            var width = width
            var height = height
            if (drawX < clipLeft) {
                width -= clipLeft - drawX
                drawX = clipLeft
            }

            if (drawY < clipBottom) {
                height -= clipBottom - drawY
                drawY = clipBottom
            }

            if (drawX + width > clipRight) {
                width = clipRight - drawX
            }

            if (drawY + height > clipTop) {
                height = clipTop - drawY
            }

            val inverseAlpha = 256 - alpha
            val r = (colour shr 16 and 0xff) * alpha
            val g = (colour shr 8 and 0xff) * alpha
            val b = (colour and 0xff) * alpha
            val dx = Raster.width - width
            var pixel = drawX + drawY * Raster.width

            for (x in 0 until height) {
                for (y in -width..-1) {
                    val r2 = (raster[pixel] shr 16 and 0xff) * inverseAlpha
                    val g2 = (raster[pixel] shr 8 and 0xff) * inverseAlpha
                    val b2 = (raster[pixel] and 0xff) * inverseAlpha
                    raster[pixel++] = (r + r2 shr 8 shl 16) + (g + g2 shr 8 shl 8) + (b + b2 shr 8)
                }

                pixel += dx
            }
        }

        fun init(height: Int, width: Int, pixels: IntArray) {
            Raster.raster = pixels
            Raster.width = width
            Raster.height = height
            setBounds(height, 0, width, 0)
        }

        fun reset() {
            val count = width * height
            for (index in 0 until count) {
                raster[index] = 0
            }
        }

        fun setBounds(clipTop: Int, clipLeft: Int, clipRight: Int, clipBottom: Int) {
            var clipTop = clipTop
            var clipLeft = clipLeft
            var clipRight = clipRight
            var clipBottom = clipBottom
            if (clipLeft < 0) {
                clipLeft = 0
            }

            if (clipBottom < 0) {
                clipBottom = 0
            }

            if (clipRight > Raster.width) {
                clipRight = Raster.width
            }

            if (clipTop > Raster.height) {
                clipTop = Raster.height
            }

            Raster.clipLeft = clipLeft
            Raster.clipBottom = clipBottom
            Raster.clipRight = clipRight
            Raster.clipTop = clipTop

            maxRight = Raster.clipRight - 1
            centreX = Raster.clipRight / 2
            centreY = Raster.clipTop / 2
        }

        fun setDefaultBounds() {
            clipLeft = 0
            clipBottom = 0
            clipRight = width
            clipTop = height
            maxRight = clipRight - 1
            centreX = clipRight / 2
        }
    }

}