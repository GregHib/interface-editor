package com.greg.model

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class Sprite(val id: Int, var data: ByteArray, val format: String): Comparable<Sprite> {

    var drawOffsetX = 0
    var drawOffsetY = 0

    override fun compareTo(other: Sprite):Int {
        return if (id > other.id) 1 else -1
    }

    override fun toString() : String {
        return id.toString()
    }

    fun getLength() : Int {
        return if (data.isEmpty()) 0 else data.size
    }

    fun toBufferdImage() : BufferedImage {
        ByteArrayInputStream(data).use {
            return ImageIO.read(it)
        }
    }

    fun toImage() : Image {
        return SwingFXUtils.toFXImage(toBufferdImage(), null)
    }

}