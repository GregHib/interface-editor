package com.greg.controller.cache

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Font
import io.nshusa.rsam.binary.Widget
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import java.nio.file.Paths

class CacheController : Controller() {

    companion object {
        var fs: IndexedFileSystem? = null
        val widgets: ObservableList<Widget> = FXCollections.observableArrayList()
    }

    fun init(directory: String) {
        fs = IndexedFileSystem.init(Paths.get(directory))
        fs?.load()
    }

    fun loadWidgets() {
        val archiveStore = fs?.getStore(FileStore.ARCHIVE_FILE_STORE)

        val widgetArchive = Archive.decode(archiveStore?.readFile(Archive.INTERFACE_ARCHIVE))
        val graphicArchive = Archive.decode(archiveStore?.readFile(Archive.MEDIA_ARCHIVE))
        val fontArchive = Archive.decode(archiveStore?.readFile(Archive.TITLE_ARCHIVE))

        val smallFont = Font.decode(fontArchive, "p11_full", false)
        val frameFont = Font.decode(fontArchive, "p12_full", false)
        val boldFont = Font.decode(fontArchive, "b12_full", false)
        val font2 = Font.decode(fontArchive, "q8_full", true)

        val fonts = arrayOf(smallFont, frameFont, boldFont, font2)

        Widget.decode(widgetArchive, graphicArchive, fonts)

        (0 until Widget.count())
                .mapNotNull { widget -> Widget.lookup(widget) }
                .filter { widget -> widget.group == Widget.TYPE_CONTAINER && widget.children != null && !widget.children.isEmpty() }
                .forEach { widget ->
                    Platform.runLater {
                        widgets.add(widget)
                    }
                }
    }
}