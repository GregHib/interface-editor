package com.greg.model.cache

import com.greg.model.cache.formats.FullCacheFormat
import com.greg.model.cache.formats.UnpackedFormat

enum class CacheTypes(val type: CacheFormat) {
    FULL_CACHE(FullCacheFormat()),
    UNPACKED_CACHE(UnpackedFormat())
}