package io.nshusa.rsam.util

import io.nshusa.rsam.binary.Widget
import io.nshusa.rsam.graphics.render.Raster
import kotlin.experimental.and

object RenderUtils {

    fun renderWidget(widget: Widget, x: Int, y: Int, scroll: Int) {
        if (widget.group == 0 && widget.children != null) {
            val clipLeft = Raster.clipLeft
            val clipBottom = Raster.clipBottom
            val clipRight = Raster.clipRight
            val clipTop = Raster.clipTop
            Raster.setBounds(y + widget.height, x, x + widget.width, y)
            val children = widget.children!!.size

            for (childIndex in 0 until children) {
                var currentX = widget.childX[childIndex] + x
                var currentY = widget.childY[childIndex] + y - scroll
                val child = Widget.lookup(widget.children!![childIndex])
                if (child != null) {
                    currentX += child.horizontalDrawOffset
                    currentY += child.verticalDrawOffset
                    if (child.contentType > 0) {
                    }

                    if (child.group == 0) {
                        if (child.scrollPosition > child.scrollLimit - child.height) {
                            child.scrollPosition = child.scrollLimit - child.height
                        }

                        if (child.scrollPosition < 0) {
                            child.scrollPosition = 0
                        }

                        renderWidget(child, currentX, currentY, child.scrollPosition)
                        if (child.scrollLimit > child.height) {
                        }
                    } else if (child.group != 1 && child.group != 2) {
                        if (child.group == 3) {
                            val colour = child.defaultColour
                            if (child.alpha.toInt() == 0) {
                                if (child.filled) {
                                    Raster.fillRectangle(currentX, currentY, child.width, child.height, colour)
                                } else {
                                    Raster.drawRectangle(currentX, currentY, child.width, child.height, colour)
                                }
                            } else if (child.filled) {
                                Raster.fillRectangle(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha and 255.toByte()))
                            } else {
                                Raster.drawRectangle(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha and 255.toByte()))
                            }
                        } else if (child.group == 4) {
                            val font = child.font
                            var text = child.defaultText
                            var colour = child.defaultColour
                            if (child.optionType == 6) {
                                text = "Please wait..."
                                colour = child.defaultColour
                            }

                            if (Raster.width == 479) {
                                if (colour == 16776960) {
                                    colour = 255
                                } else if (colour == 49152) {
                                    colour = 16777215
                                }
                            }

                            var drawY = currentY + font.verticalSpace
                            while (text.length > 0) {
                                val line = text.indexOf("\\n")
                                val drawn: String
                                if (line != -1) {
                                    drawn = text.substring(0, line)
                                    text = text.substring(line + 2)
                                } else {
                                    drawn = text
                                    text = ""
                                }

                                if (child.centeredText) {
                                    font.shadowCentre(currentX + child.width / 2, drawY, drawn, child.shadowedText, colour)
                                } else {
                                    font.shadow(currentX, drawY, drawn, child.shadowedText, colour)
                                }
                                drawY += font.verticalSpace
                            }
                        } else if (child.group == 5) {
                            val sprite = child.defaultSprite
                            sprite?.drawSprite(currentX, currentY)
                        } else if (child.group != 6 && child.group == 7) {
                        }
                    }
                }
            }

            Raster.setBounds(clipTop, clipLeft, clipRight, clipBottom)
        }
    }

    fun renderRectangle(child: Widget, currentX: Int, currentY: Int) {
        if (child.group == 3) {
            val colour = child.defaultColour
            if (child.alpha.toInt() == 0) {
                if (child.filled) {
                    Raster.fillRectangle(currentX, currentY, child.width, child.height, colour)
                } else {
                    Raster.drawRectangle(currentX, currentY, child.width, child.height, colour)
                }
            } else if (child.filled) {
                Raster.fillRectangle(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha and 255.toByte()))
            } else {
                Raster.drawRectangle(currentX, currentY, child.width, child.height, colour, 256 - (child.alpha and 255.toByte()))
            }
        }

    }

    fun renderText(child: Widget, x: Int, y: Int) {
        if (child.group == 4) {
            val font = child.font
            var text = child.defaultText
            var colour = child.defaultColour
            if (child.optionType == 6) {
                text = "Please wait..."
                colour = child.defaultColour
            }

            if (Raster.width == 479) {
                if (colour == 16776960) {
                    colour = 255
                } else if (colour == 49152) {
                    colour = 16777215
                }
            }

            var drawY = y + font.verticalSpace
            while (text.isNotEmpty()) {
                val line = text.indexOf("\\n")
                val drawn: String
                if (line != -1) {
                    drawn = text.substring(0, line)
                    text = text.substring(line + 2)
                } else {
                    drawn = text
                    text = ""
                }

                if (child.centeredText) {
                    font.shadowCentre(x + child.width / 2, drawY, drawn, child.shadowedText, colour)
                } else {
                    font.shadow(x, drawY, drawn, child.shadowedText, colour)
                }
                drawY += font.verticalSpace
            }
        }

    }
}