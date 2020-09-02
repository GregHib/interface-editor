package rs.dusk.cache.definition.data

import java.awt.image.BufferedImage

class SpriteGroup(val hash: Int, val width: Int, val height: Int, size: Int, val sprites: Array<BufferedImage?> = arrayOfNulls(size))