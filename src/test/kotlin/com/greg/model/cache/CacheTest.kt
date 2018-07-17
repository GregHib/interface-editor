package com.greg.model.cache

import org.junit.Assert
import org.junit.Test
import java.io.File

class CacheTest {

    private val path = CachePath(File("./cache/"))
    private var cache = Cache(path)

    @Test
    fun loadSprites() {
        Assert.assertTrue(cache.loadSprites() > 0)
    }

    @Test
    fun loadFonts() {
    }

    @Test
    fun loadInterface() {
    }
}