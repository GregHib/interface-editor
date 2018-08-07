package com.greg.model.cache.archives

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.Cache
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.WidgetRectangle
import com.greg.model.widgets.type.WidgetSprite
import com.greg.model.widgets.type.WidgetText
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.TreeItem
import javafx.scene.paint.Color
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

    fun getColour(colour: Int): Color {
        val red = colour shr 16 and 0xff
        val green = colour shr 8 and 0xff
        val blue = colour and 0xff
        return Color(red / 255.0, green / 255.0, blue / 255.0, 1.0)
    }


    fun display(widgets: WidgetsController, index: Int, x: Int = 0, y: Int = 0) {

        val container = Widget.lookup(index) ?: return

        if (container.group != Widget.TYPE_CONTAINER || container.children?.isEmpty() ?: return)
            return

        val len = container.children?.size ?: return

        for (id in 0 until len) {
            val child = Widget.lookup(container.children!![id]) ?: continue

            val childX = container.childX[id] + x
            val childY = container.childY[id] + y

            if(child.group == Widget.TYPE_CONTAINER) {
                display(widgets, child.id, childX, childY)
                continue
            }

            val widget = WidgetBuilder(when(child.group) {
                Widget.TYPE_SPRITE -> WidgetType.SPRITE
                Widget.TYPE_RECTANGLE -> WidgetType.RECTANGLE
                Widget.TYPE_TEXT -> WidgetType.TEXT
                else -> WidgetType.WIDGET
            }).build()

            when(widget) {
                is WidgetSprite -> {
                    if(child.defaultSpriteArchive != null)
                        widget.setArchive(child.defaultSpriteArchive!!)
                    if(child.defaultSpriteIndex != null)
                        widget.setSprite(child.defaultSpriteIndex!!)
                }
                is WidgetText -> {
                    widget.setText(child.defaultText)
                    widget.setColour(getColour(child.defaultColour))
                }
                is WidgetRectangle -> {
                    widget.setFill(getColour(child.defaultColour))
                    widget.setStroke(getColour(child.defaultColour))
                }
            }

            widgets.add(widget)

            widget.setWidth(child.width)
            widget.setHeight(child.height)
            widget.setX(childX)
            widget.setY(childY)
        }


//        widgets.addAll(children.toTypedArray())
    }
}