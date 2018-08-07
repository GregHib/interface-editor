package com.greg.model.cache.archives

import com.greg.model.cache.Cache
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.TreeItem

class ArchiveInterface : CacheArchive() {

    override fun reset(): Boolean {
        return true
    }

    override fun load(cache: Cache): Boolean {
        return try {
            val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

            Widget.decode(archive, null, null)

            for (widget in 0 until Widget.count()) {

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
            }

            true
        } catch (e: NullPointerException) {
            e.printStackTrace()
            cache.reset()
            false
        }
    }


}