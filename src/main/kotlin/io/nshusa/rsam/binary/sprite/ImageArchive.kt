package io.nshusa.rsam.binary.sprite

import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.util.HashUtils

import java.io.IOException
import java.util.ArrayList

class ImageArchive(var hash: Int) {
    private val sprites = ArrayList<Sprite>()

    fun setName(name: String) {
        this.hash = HashUtils.nameToHash(name)
    }

    fun getSprites(): List<Sprite> {
        return sprites
    }

    companion object {

        fun decode(archive: Archive, hash: Int): ImageArchive {
            val imageArchive = ImageArchive(hash)

            var i = 0
            while (true) {
                try {
                    val decoded = Sprite.decode(archive, hash = hash, id = i)

                    imageArchive.sprites.add(decoded)
                } catch (e: IOException) {
                    break
                }

                i++
            }

            return imageArchive
        }

        fun decode(archive: Archive, name: String): ImageArchive {
            return decode(archive, HashUtils.nameToHash(name))
        }
    }

}