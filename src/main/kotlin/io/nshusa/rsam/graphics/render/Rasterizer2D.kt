package io.nshusa.rsam.graphics.render

open class Rasterizer2D {
    companion object {
        lateinit var depthBuffer: FloatArray
        var pixels: IntArray? = null
        var width: Int = 0
        var height: Int = 0
        var topY: Int = 0
        var bottomY: Int = 0
        var leftX: Int = 0
        var bottomX: Int = 0
        var lastX: Int = 0
        var viewportCenterX: Int = 0
        var viewportCenterY: Int = 0

        fun initDrawingArea(height: Int, width: Int, pixels: IntArray, depth: FloatArray) {
            var height = height
            var width = width
            var pixels = pixels
            depthBuffer = depth
            pixels = pixels
            width = width
            height = height
            setDrawingArea(height, 0, width, 0)
        }

        fun drawTransparentGradientBox(leftX: Int, topY: Int, width: Int, height: Int, topColour: Int, bottomColour: Int, opacity: Int) {
            var leftX = leftX
            var topY = topY
            var width = width
            var height = height
            var gradientProgress = 0
            val progressPerPixel = 65536 / height
            if (leftX < leftX) {
                width -= leftX - leftX
                leftX = leftX
            }

            if (topY < topY) {
                gradientProgress += (topY - topY) * progressPerPixel
                height -= topY - topY
                topY = topY
            }

            if (leftX + width > bottomX) {
                width = bottomX - leftX
            }

            if (topY + height > bottomY) {
                height = bottomY - topY
            }

            val leftOver = width - width
            val transparency = 256 - opacity
            var pixelIndex = leftX + topY * width

            for (rowIndex in 0 until height) {
                val gradient = 65536 - gradientProgress shr 8
                val inverseGradient = gradientProgress shr 8
                val gradientColour = (((topColour and 16711935) * gradient + (bottomColour and 16711935) * inverseGradient and -16711936) + ((topColour and '\uff00'.toInt()) * gradient + (bottomColour and '\uff00'.toInt()) * inverseGradient and 16711680)).ushr(8)
                val transparentPixel = ((gradientColour and 16711935) * opacity shr 8 and 16711935) + ((gradientColour and '\uff00'.toInt()) * opacity shr 8 and '\uff00'.toInt())

                for (columnIndex in 0 until width) {
                    var backgroundPixel = pixels!![pixelIndex]
                    backgroundPixel = ((backgroundPixel and 16711935) * transparency shr 8 and 16711935) + ((backgroundPixel and '\uff00'.toInt()) * transparency shr 8 and '\uff00'.toInt())
                    pixels!![pixelIndex++] = transparentPixel + backgroundPixel
                }

                pixelIndex += leftOver
                gradientProgress += progressPerPixel
            }

        }

        fun defaultDrawingAreaSize() {
            leftX = 0
            topY = 0
            bottomX = width
            bottomY = height
            lastX = bottomX
            viewportCenterX = bottomX / 2
        }

        fun setDrawingArea(bottomY: Int, leftX: Int, rightX: Int, topY: Int) {
            var bottomY = bottomY
            var leftX = leftX
            var rightX = rightX
            var topY = topY
            if (leftX < 0) {
                leftX = 0
            }

            if (topY < 0) {
                topY = 0
            }

            if (rightX > width) {
                rightX = width
            }

            if (bottomY > height) {
                bottomY = height
            }

            leftX = leftX
            topY = topY
            bottomX = rightX
            bottomY = bottomY
            lastX = bottomX
            viewportCenterX = bottomX / 2
            viewportCenterY = bottomY / 2
        }

        fun clear() {
            val i = width * height

            for (j in 0 until i) {
                pixels!![j] = 0
                depthBuffer[j] = 3.4028235E38f
            }

        }

        fun drawBox(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int) {
            var leftX = leftX
            var topY = topY
            var width = width
            var height = height
            if (leftX < leftX) {
                width -= leftX - leftX
                leftX = leftX
            }

            if (topY < topY) {
                height -= topY - topY
                topY = topY
            }

            if (leftX + width > bottomX) {
                width = bottomX - leftX
            }

            if (topY + height > bottomY) {
                height = bottomY - topY
            }

            val leftOver = width - width
            var pixelIndex = leftX + topY * width

            for (rowIndex in 0 until height) {
                for (columnIndex in 0 until width) {
                    pixels!![pixelIndex++] = rgbColour
                }

                pixelIndex += leftOver
            }

        }

        fun drawTransparentBox(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int, opacity: Int) {
            var leftX = leftX
            var topY = topY
            var width = width
            var height = height
            if (leftX < leftX) {
                width -= leftX - leftX
                leftX = leftX
            }

            if (topY < topY) {
                height -= topY - topY
                topY = topY
            }

            if (leftX + width > bottomX) {
                width = bottomX - leftX
            }

            if (topY + height > bottomY) {
                height = bottomY - topY
            }

            val transparency = 256 - opacity
            val red = (rgbColour shr 16 and 255) * opacity
            val green = (rgbColour shr 8 and 255) * opacity
            val blue = (rgbColour and 255) * opacity
            val leftOver = width - width
            var pixelIndex = leftX + topY * width

            for (rowIndex in 0 until height) {
                for (columnIndex in 0 until width) {
                    val otherRed = (pixels!![pixelIndex] shr 16 and 255) * transparency
                    val otherGreen = (pixels!![pixelIndex] shr 8 and 255) * transparency
                    val otherBlue = (pixels!![pixelIndex] and 255) * transparency
                    val transparentColour = (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
                    pixels!![pixelIndex++] = transparentColour
                }

                pixelIndex += leftOver
            }

        }

        fun drawBoxOutline(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int) {
            drawHorizontalLine(leftX, topY, width, rgbColour)
            drawHorizontalLine(leftX, topY + height - 1, width, rgbColour)
            drawVerticalLine(leftX, topY, height, rgbColour)
            drawVerticalLine(leftX + width - 1, topY, height, rgbColour)
        }

        fun drawHorizontalLine(xPosition: Int, yPosition: Int, width: Int, rgbColour: Int) {
            var xPosition = xPosition
            var width = width
            if (yPosition >= topY && yPosition < bottomY) {
                if (xPosition < leftX) {
                    width -= leftX - xPosition
                    xPosition = leftX
                }

                if (xPosition + width > bottomX) {
                    width = bottomX - xPosition
                }

                val pixelIndex = xPosition + yPosition * width

                for (i in 0 until width) {
                    pixels!![pixelIndex + i] = rgbColour
                }

            }
        }

        fun drawVerticalLine(xPosition: Int, yPosition: Int, height: Int, rgbColour: Int) {
            var yPosition = yPosition
            var height = height
            if (xPosition >= leftX && xPosition < bottomX) {
                if (yPosition < topY) {
                    height -= topY - yPosition
                    yPosition = topY
                }

                if (yPosition + height > bottomY) {
                    height = bottomY - yPosition
                }

                val pixelIndex = xPosition + yPosition * width

                for (rowIndex in 0 until height) {
                    pixels!![pixelIndex + rowIndex * width] = rgbColour
                }

            }
        }

        fun drawTransparentBoxOutline(leftX: Int, topY: Int, width: Int, height: Int, rgbColour: Int, opacity: Int) {
            drawTransparentHorizontalLine(leftX, topY, width, rgbColour, opacity)
            drawTransparentHorizontalLine(leftX, topY + height - 1, width, rgbColour, opacity)
            if (height >= 3) {
                drawTransparentVerticalLine(leftX, topY + 1, height - 2, rgbColour, opacity)
                drawTransparentVerticalLine(leftX + width - 1, topY + 1, height - 2, rgbColour, opacity)
            }

        }

        fun drawTransparentHorizontalLine(xPosition: Int, yPosition: Int, width: Int, rgbColour: Int, opacity: Int) {
            var xPosition = xPosition
            var width = width
            if (yPosition >= topY && yPosition < bottomY) {
                if (xPosition < leftX) {
                    width -= leftX - xPosition
                    xPosition = leftX
                }

                if (xPosition + width > bottomX) {
                    width = bottomX - xPosition
                }

                val transparency = 256 - opacity
                val red = (rgbColour shr 16 and 255) * opacity
                val green = (rgbColour shr 8 and 255) * opacity
                val blue = (rgbColour and 255) * opacity
                var pixelIndex = xPosition + yPosition * width

                for (i in 0 until width) {
                    val otherRed = (pixels!![pixelIndex] shr 16 and 255) * transparency
                    val otherGreen = (pixels!![pixelIndex] shr 8 and 255) * transparency
                    val otherBlue = (pixels!![pixelIndex] and 255) * transparency
                    val transparentColour = (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
                    pixels!![pixelIndex++] = transparentColour
                }

            }
        }

        fun drawTransparentVerticalLine(xPosition: Int, yPosition: Int, height: Int, rgbColour: Int, opacity: Int) {
            var yPosition = yPosition
            var height = height
            if (xPosition >= leftX && xPosition < bottomX) {
                if (yPosition < topY) {
                    height -= topY - yPosition
                    yPosition = topY
                }

                if (yPosition + height > bottomY) {
                    height = bottomY - yPosition
                }

                val transparency = 256 - opacity
                val red = (rgbColour shr 16 and 255) * opacity
                val green = (rgbColour shr 8 and 255) * opacity
                val blue = (rgbColour and 255) * opacity
                var pixelIndex = xPosition + yPosition * width

                for (i in 0 until height) {
                    val otherRed = (pixels!![pixelIndex] shr 16 and 255) * transparency
                    val otherGreen = (pixels!![pixelIndex] shr 8 and 255) * transparency
                    val otherBlue = (pixels!![pixelIndex] and 255) * transparency
                    val transparentColour = (red + otherRed shr 8 shl 16) + (green + otherGreen shr 8 shl 8) + (blue + otherBlue shr 8)
                    pixels!![pixelIndex] = transparentColour
                    pixelIndex += width
                }

            }
        }
    }
}