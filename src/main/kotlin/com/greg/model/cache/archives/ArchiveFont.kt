package com.greg.model.cache.archives

import javafx.scene.effect.BlurType
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font.*
import javafx.scene.text.FontWeight
import rs.dusk.cache.Cache

class ArchiveFont : CacheArchive() {

    companion object {

        val shadow = DropShadow()
        val small = font(11.0)
        val medium = font(12.0)
        val bold = font(null, FontWeight.BLACK, 12.0)
        val thin = font(null, FontWeight.THIN, 14.0)

        init {
            shadow.blurType = BlurType.GAUSSIAN
            shadow.radius = 1.0
            shadow.offsetX = 1.0
            shadow.offsetY = 1.0
            shadow.color = Color.color(0.0, 0.0, 0.0)
        }
    }

    override fun reset(): Boolean {
        return true
    }

    override fun load(cache: Cache): Boolean {
        return true
    }

}