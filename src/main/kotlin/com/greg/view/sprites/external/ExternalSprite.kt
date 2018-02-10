package com.greg.view.sprites.external

import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.graphics.render.Rasterizer2D
import java.awt.Toolkit
import java.awt.image.PixelGrabber
import javax.swing.ImageIcon

class ExternalSprite(val id: Int, data: ByteArray) : Comparable<ExternalSprite> {

    val sprite = createSprite(data)
    val isEmpty = data.isEmpty()

    override fun compareTo(other: ExternalSprite): Int {
        return if (id > other.id) 1 else -1
    }

    private fun createSprite(spriteData: ByteArray): Sprite {
        val sprite = Sprite()
        try {
            val image: java.awt.Image? = Toolkit.getDefaultToolkit().createImage(spriteData)
            val icon = ImageIcon(image!!)
            Rasterizer2D.width = icon.iconWidth
            Rasterizer2D.height = icon.iconHeight
            sprite.width = Rasterizer2D.width
            sprite.height = Rasterizer2D.height
            sprite.offsetX = 0
            sprite.offsetY = 0
            Rasterizer2D.pixels = IntArray(Rasterizer2D.width * Rasterizer2D.height)
            val pixelgrabber = PixelGrabber(image, 0, 0, Rasterizer2D.width, Rasterizer2D.height, Rasterizer2D.pixels, 0, Rasterizer2D.width)
            pixelgrabber.grabPixels()
            sprite.pixels = Rasterizer2D.pixels
        } catch (_ex: Exception) {
            println(_ex)
        }
        return sprite
    }
}