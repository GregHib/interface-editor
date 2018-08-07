package com.greg.model.cache

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Font
import io.nshusa.rsam.binary.Widget
import org.junit.Assert
import org.junit.Test
import java.io.IOException

class CacheTest {

    private val path = CachePath("./cache/")
    private var cache = Cache(path)

    private val filePath = CachePath("./cache/interface.jag")
    private var fileCache = Cache(filePath)

    @Test
    fun readFile() {
        fileCache.use { fs ->
            try {
                Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.MEDIA_ARCHIVE))
                Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))
                Assert.fail()
            } catch (e: NullPointerException) {
            }
        }
    }

    @Test
    fun loadSprites() {
//        Assert.assertTrue(cache.loadSprites() > 0)
//        Assert.assertTrue(fileCache.loadSprites() == 0)
    }

    @Test
    fun loadFonts() {
        cache.use { fs ->

            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE))

            loadFont(archive, "p11_full", false)
            loadFont(archive, "p12_full", false)
            loadFont(archive, "b12_full", false)
            loadFont(archive, "q8_full", true)
        }
    }

    private fun loadFont(archive: Archive, name: String, wideSpace: Boolean) {
        try {
            Font.decode(archive, name, wideSpace)
        } catch (e: IOException) {
            e.printStackTrace()
            Assert.fail()
        }
    }

    @Test
    fun loadInterface() {

        cache.use { fs ->

            val archive = Archive.decode(fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

            Widget.decode(archive, null, null)

            /*for (widget in 0 until Widget.count()) {

                val container = Widget.lookup(widget) ?: continue

                if (container.group != Widget.TYPE_CONTAINER || container.children?.isEmpty() ?: continue) {
                    continue
                }

                val len = container.children?.size ?: continue

                val treeItem = TreeItem(container)

                for (id in 0 until len) {
                    val child = Widget.lookup(container.children!![id]) ?: Widget(container.children!![id])

                    val childTreeItem = TreeItem(child)

                    treeItem.children.add(childTreeItem)
                }
            }*/

            Assert.assertTrue(Widget.count() > 0)
        }
    }
}