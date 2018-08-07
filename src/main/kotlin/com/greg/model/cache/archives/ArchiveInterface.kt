package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.Cache
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.TreeItem
import java.io.InputStream

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

//                println("$widget ${getName(widget)}")

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


    fun getName(index: Int): String {
        //Get known hashes
        val inputStream: InputStream = javaClass.getResourceAsStream("interfaces.txt")
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }

        //If match return description/name
        lineList.forEach {
            val split = it.split("   ")
            val id = split[0].toInt()
            if (id == index)
                return split[1]
        }
        //Otherwise just return the hash id
        return index.toString()
    }

    fun display(widgets: WidgetsController, id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}