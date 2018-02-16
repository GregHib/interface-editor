package com.greg.view.sprites.external

import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.graphics.render.Rasterizer2D
import java.awt.Image
import java.awt.Toolkit
import java.awt.image.PixelGrabber
import javax.swing.ImageIcon

/**
 * Used to store sprite data to load later when outside of a thread
 */
class SpriteLoader(private val data: ByteArray, private val offsetX: Int, private val offsetY: Int) {

    fun load(): Sprite {
        val sprite = createSprite(data)
        sprite.offsetX = offsetX
        sprite.offsetY = offsetY
        return sprite
    }

    private fun createSprite(spriteData: ByteArray): Sprite {
        val sprite = Sprite()
        try {
            val image: Image? = Toolkit.getDefaultToolkit().createImage(spriteData)
            val icon = ImageIcon(image!!)
            Rasterizer2D.width = icon.iconWidth
            Rasterizer2D.height = icon.iconHeight
            sprite.width = Rasterizer2D.width
            sprite.height = Rasterizer2D.height
            sprite.offsetX = 0
            sprite.offsetY = 0
            Rasterizer2D.pixels = IntArray(Rasterizer2D.width * Rasterizer2D.height)
            val grabber = PixelGrabber(image, 0, 0, Rasterizer2D.width, Rasterizer2D.height, Rasterizer2D.pixels, 0, Rasterizer2D.width)
            grabber.grabPixels()
            sprite.pixels = Rasterizer2D.pixels
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sprite
    }

}