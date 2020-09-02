package rs.dusk.cache.definition.decoder

import rs.dusk.cache.definition.data.SpriteGroup
import rs.dusk.core.io.read.Reader
import java.awt.image.BufferedImage

class SpriteDecoder {
    fun decode(reader: Reader, id: Int): SpriteGroup {
        reader.position(reader.length - 2)
        val size = reader.readShort()

        val childX = IntArray(size)
        val childY = IntArray(size)
        val childWidth = IntArray(size)
        val childHeight = IntArray(size)
        reader.position((reader.length - (size * 8)) - 7)

        val groupWidth = reader.readShort()
        val groupHeight = reader.readShort()

        val palette = IntArray(reader.readUnsignedByte() + 1)

        val group = SpriteGroup(id, groupWidth, groupHeight, size)

        repeat(size) {
            childX[it] = reader.readUnsignedShort()
        }
        repeat(size) {
            childY[it] = reader.readUnsignedShort()
        }
        repeat(size) {
            childWidth[it] = reader.readUnsignedShort()
        }
        repeat(size) {
            childHeight[it] = reader.readUnsignedShort()
        }
        reader.position((reader.length - (size * 8) - 7) - (palette.size - 1) * 3)
        palette[0] = 0
        for (index in 1 until palette.size) {
            palette[index] = reader.readMedium()
            if (palette[index] == 0) {
                palette[index] = 1
            }
        }

        reader.position(0)
        for (id in 0 until size) {
            val subWidth = childWidth[id]
            val subHeight = childHeight[id]
            val offsetX = childX[id]
            val offsetY = childY[id]

            if (subWidth > 1000 || subHeight > 1000 || groupWidth > 1000 || groupHeight > 1000) {
                continue
            }

            group.sprites[id] = BufferedImage(groupWidth, groupHeight, BufferedImage.TYPE_INT_ARGB)
            val image = group.sprites[id]!!

            val indices = Array(subWidth) { IntArray(subHeight) }

            val flags = reader.readByte()
            if (flags and FLAG_VERTICAL != 0) {
                repeat(subWidth) { x ->
                    repeat(subHeight) { y ->
                        indices[x][y] = reader.readUnsignedByte()
                    }
                }
            } else {
                repeat(subHeight) { y ->
                    repeat(subWidth) { x ->
                        indices[x][y] = reader.readUnsignedByte()
                    }
                }
            }

            if (flags and FLAG_ALPHA != 0) {
                if (flags and FLAG_VERTICAL != 0) {
                    repeat(subWidth) { x ->
                        repeat(subHeight) { y ->
                            val alpha: Int = reader.readUnsignedByte()
                            image.setRGB(x + offsetX, y + offsetY, alpha shl 24 or palette[indices[x][y]])
                        }
                    }
                } else {
                    repeat(subHeight) { y ->
                        repeat(subWidth) { x ->
                            val alpha: Int = reader.readUnsignedByte()
                            image.setRGB(x + offsetX, y + offsetY, alpha shl 24 or palette[indices[x][y]])
                        }
                    }
                }
            } else {
                repeat(subWidth) { x ->
                    repeat(subHeight) { y ->
                        val index: Int = indices[x][y]
                        if (index == 0) {
                            image.setRGB(x + offsetX, y + offsetY, 0)
                        } else {
                            image.setRGB(x + offsetX, y + offsetY, -0x1000000 or palette[index])
                        }
                    }
                }
            }
        }
        return group
    }

    companion object {

        const val FLAG_VERTICAL = 0x01
        const val FLAG_ALPHA = 0x02
    }
}