package rs.dusk.cache.definition.data

data class IndexedSprite(
    var offsetX: Int = 0,
    var offsetY: Int = 0,
    var width: Int = 0,
    var height: Int = 0,
    var deltaWidth: Int = 0,
    var deltaHeight: Int = 0
) {
    lateinit var palette: IntArray
    lateinit var raster: ByteArray
    var alpha: ByteArray? = null
}