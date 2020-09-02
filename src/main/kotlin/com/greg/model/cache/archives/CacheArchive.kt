package com.greg.model.cache.archives

import rs.dusk.cache.Cache

abstract class CacheArchive {
    abstract fun load(cache: Cache): Boolean

    abstract fun reset(): Boolean

    var loaded = false

}