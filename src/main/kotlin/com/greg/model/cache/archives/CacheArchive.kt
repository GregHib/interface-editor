package com.greg.model.cache.archives

import com.greg.model.cache.Cache

abstract class CacheArchive {
    abstract fun load(cache: Cache): Boolean

    abstract fun reset(): Boolean

    var loaded = false

}