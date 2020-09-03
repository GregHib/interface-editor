package rs.dusk.cache.definition.data

import rs.dusk.cache.Definition

@Suppress("ArrayInDataClass")
data class SpriteDefinition(
    override var id: Int = -1,
    var sprites: Array<IndexedSprite>? = null
) : Definition