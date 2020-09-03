package com.greg.model.cache.archives

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import rs.dusk.cache.Cache
import rs.dusk.cache.definition.data.IndexedSprite
import rs.dusk.cache.definition.data.SpriteDefinition
import rs.dusk.cache.definition.decoder.SpriteDecoder

class ArchiveMedia : CacheArchive() {

    companion object {
        internal lateinit var decoder: SpriteDecoder
        var imageArchive: ObservableList<SpriteDefinition> = FXCollections.observableArrayList()

        fun getDef(id: Int): SpriteDefinition? {
            return decoder.getOrNull(id)
        }

        fun getImage(id: Int, index: Int): Image? {
            val def = decoder.getOrNull(id) ?: return null
            return def.sprites?.getOrNull(index)?.toImage()
        }

        private fun IndexedSprite.toImage(): Image {
            val width = deltaWidth + width + offsetX
            val height = deltaHeight + height + offsetY
            val output = WritableImage(width, height)
            val writer = output.pixelWriter
            val argb = rgba()
            repeat(width) { x ->
                repeat(height) { y ->
                    writer.setArgb(x, y, argb[x + y * width])
                }
            }
            return output
        }

        private fun IndexedSprite.rgba(): IntArray {
            val canvasWidth: Int = width + offsetX + deltaWidth
            val canvasHeight: Int = height + offsetY + deltaHeight
            val out = IntArray(canvasWidth * canvasHeight)
            if (alpha == null) {
                for (y in 0 until height) {
                    var s = y * width
                    var t: Int = offsetX + (y + this.offsetY) * canvasWidth
                    for (x in 0 until width) {
                        val b = palette[raster[s++].toInt() and 0xff]
                        if (b == 0) {
                            out[t++] = 0
                        } else {
                            out[t++] = -16777216 or b
                        }
                    }
                }
            } else {
                for (y in 0 until height) {
                    var s = y * width
                    var t: Int = offsetX + (y + this.offsetY) * canvasWidth
                    for (x in 0 until width) {
                        out[t++] = alpha!![s].toInt() shl 24 or palette[raster[s].toInt() and 0xff]
                        s++
                    }
                }
            }
            return out
        }
    }

    override fun load(cache: Cache): Boolean {
        decoder = SpriteDecoder(cache)
        println("Found ${cache.lastArchiveId(8)} sprite groups.")
        return true
    }

    override fun reset(): Boolean {
        imageArchive.clear()
        return true
    }
}