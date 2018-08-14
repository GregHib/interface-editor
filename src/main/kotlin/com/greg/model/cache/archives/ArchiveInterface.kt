package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.Cache
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.cache.archives.widget.WidgetDataIO
import com.greg.model.cache.formats.CacheFormats
import com.greg.model.widgets.type.Widget
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import org.apache.commons.io.FileUtils
import java.io.InputStream

class ArchiveInterface : CacheArchive() {

    companion object {
        var widgetsData: Array<WidgetData?>? = null

        fun lookup(id: Int): WidgetData? {
            return if (widgetsData == null) null else widgetsData!![id]
        }

        fun updateData(widget: Widget) {
            widgetsData!![widget.identifier] = WidgetDataConverter.toData(widget)
        }
    }

    fun save(widgets: WidgetsController, cache: Cache): Boolean {
        if(!cache.path.isValid())
            return false

        //Update all the widgets currently in use into the WidgetData list
        widgets.getAll().forEach { widget -> ArchiveInterface.updateData(widget) }

        val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))

        //Write all WidgetData to buffer
        val buffer = WidgetDataIO.write()

        //Write the data to the Interface.jag archive
        archive.writeFile("data", buffer.array())

        //Re-encode the archive
        val encoded = archive.encode()

        return when(cache.getCacheType()) {
            CacheFormats.FULL_CACHE -> {
                //Write the update to the cache.
                cache.writeFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE, encoded)
            }
            CacheFormats.UNPACKED_CACHE -> {
                val file = cache.path.getArchiveFile(cache.path.getFiles(), Archive.INTERFACE_ARCHIVE)
                //Overwrite
                FileUtils.writeByteArrayToFile(file, encoded)
                true
            }
        }
    }

    override fun reset(): Boolean {
        return true
    }

    override fun load(cache: Cache): Boolean {
        val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))
        val buffer = archive.readFile("data")
        val data = WidgetDataIO.read(buffer) ?: return false

        widgetsData = data
        return true
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

    fun display(widgets: WidgetsController, index: Int, x: Int = 0, y: Int = 0) {

        val container = lookup(index) ?: return

        if (container.group != WidgetData.TYPE_CONTAINER || container.children?.isEmpty() ?: return)
            return

        val children = WidgetDataConverter.toChildren(index, x, y)

        widgets.addAll(children.toTypedArray())
    }
}