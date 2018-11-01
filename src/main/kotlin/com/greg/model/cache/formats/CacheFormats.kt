package com.greg.model.cache.formats

enum class CacheFormats(val type: CacheFormat) {
    FULL_CACHE(FullCacheFormat()),
    UNPACKED_CACHE(UnpackedFormat())
}