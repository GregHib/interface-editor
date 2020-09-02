package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.OldCache
import com.greg.model.cache.archives.widget.SpriteData
import com.greg.model.cache.archives.widget.WidgetData
import com.greg.model.cache.archives.widget.WidgetDataConverter
import com.greg.model.cache.archives.widget.WidgetDataIO
import com.greg.model.cache.formats.CacheFormats
import com.greg.model.widgets.type.Widget
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import org.apache.commons.io.FileUtils
import rs.dusk.cache.Cache
import rs.dusk.cache.definition.decoder.InterfaceDecoder
import java.io.InputStream

class ArchiveInterface : CacheArchive() {

    companion object {
        private var widgetsData = arrayOfNulls<WidgetData>(Short.MAX_VALUE.toInt())

        fun lookup(id: Int): WidgetData? {
            return widgetsData[id]
        }

        fun updateData(widget: Widget) {
            widgetsData[widget.identifier] = widget.toData()
        }

        private fun clear() {
            widgetsData = arrayOfNulls(Short.MAX_VALUE.toInt())
        }

        fun set(widgets: Array<WidgetData?>) {
            clear()
            widgets.filterNotNull().forEachIndexed { index, data ->
                widgetsData[index] = data
            }
        }

        fun set(widgets: WidgetData) {
            widgetsData[widgets.id] = widgets
        }

        fun get(): Array<WidgetData?> {
            return widgetsData
        }
    }

    fun save(widgets: WidgetsController, cache: OldCache): Boolean {
        if(!cache.path.isValid())
            return false

        //Update all the widgets currently in use into the WidgetData list
        widgets.forAll { widget -> ArchiveInterface.updateData(widget) }

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
        clear()
        return true
    }

    override fun load(cache: Cache): Boolean {
        val decoder = InterfaceDecoder(cache)
        val list = arrayOfNulls<WidgetData>(Short.MAX_VALUE.toInt())
        repeat(decoder.size) { id ->
            val def = decoder.getOrNull(id) ?: return@repeat
            val children = def.components?.map { (componentId, component) ->
                WidgetData(componentId).apply {
                    parent = id
                    group = component.type
                    x = component.basePositionX
                    y = component.basePositionY
                    width = component.baseWidth
                    height = component.baseHeight
                    alpha = component.alpha.toByte()
                    hidden = component.hidden
                    centeredText = !component.centreType
                    shadowedText = component.shaded
                    defaultColour = component.colour
                    secondaryColour = component.backgroundColour
                    filled = component.filled
                    defaultText = component.text
                    secondaryText = component.applyText
                    spriteScale = component.spriteScale
                    spritePitch = component.spritePitch
                    spriteRoll = component.spriteRoll
                    repeats = component.imageRepeat
                    if(component.defaultImage != -1) {
                        defaultSpriteArchive = component.defaultImage.toString()
                        defaultSpriteIndex = 0
                    }
                }
            }
            list[id] = WidgetData(id).apply {
                this.width = 600
                this.height = 400
                this.children = children?.toTypedArray()
            }
        }
//        val archive = Archive.decode(cache.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE))
//        val buffer = archive.readFile("data")
//        var data = WidgetDataIO.read(buffer) ?: return false
//
//        //Children converted after as likely child.id > parent.id so child data wouldn't have been loaded
//        data = convert(data)

        set(list)
        return true
    }

    /**
     * Converts widgetIndex, childX & childY arrays to WidgetData array
     * Has to be called with complete list as child id is likely to be greater than parent id
     * @param widgetsData Complete list of WidgetData
     * @return widgetsData with children indices converted to WidgetData
     */
    private fun convert(widgetsData: Array<WidgetData?>): Array<WidgetData?> {
        val widgetsList = widgetsData.filterNotNull().toTypedArray()
        //For all interfaces
        return widgetsData.map { widget ->
            if(widget == null)
                return@map widget

            //Convert child indices to actual WidgetData with children
            if(widget.childIndices != null && widget.childIndices!!.isNotEmpty())
                convert(widgetsList, widget)
            else
                widget
        }.toTypedArray()
    }

    /**
     * Sub convert function, used for iteration any number of children of children etc..
     * @param widgetsData Complete list of WidgetData
     * @param child The WidgetData to convert
     * @return child The WidgetData once children have been converted
     */
    private fun convert(widgetsData: Array<WidgetData>, child: WidgetData): WidgetData {
        if(child.childIndices != null && child.childIndices!!.isNotEmpty()) {
            child.children = child.childIndices!!.mapIndexed { index, i ->
                val c = convert(widgetsData, widgetsData[i].clone())
                c.x = child.childX!![index]
                c.y = child.childY!![index]
                c
            }.toTypedArray()

            //Not necessary just a lil memory saving
            child.childIndices = null
            child.childX = null
            child.childY = null
        }
        return child
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

    fun display(widgets: WidgetsController, index: Int) {
        val data = lookup(index) ?: return

        if (data.group != WidgetData.TYPE_CONTAINER || data.children?.isEmpty() ?: return)
            return

        val widget = WidgetDataConverter.create(data)
        widgets.add(widget)
    }
}