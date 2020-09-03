package com.greg.model.cache

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.archives.ArchiveFont
import com.greg.model.cache.archives.ArchiveInterface
import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.settings.Settings
import com.greg.model.widgets.WidgetBuilder
import javafx.scene.control.Alert
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import rs.dusk.cache.Cache
import rs.dusk.cache.CacheDelegate
import tornadofx.Controller
import tornadofx.alert
import java.io.File
import java.io.FileNotFoundException

class CacheController : Controller() {

    var path: CachePath? = null

//    var cache = OldCache(CachePath("./"))

    val fonts = ArchiveFont()
    val sprites = ArchiveMedia()
    val interfaces = ArchiveInterface()

    var loaded = false

    init {
        val dir = Settings.get(Settings.CACHE_DIRECTORY)
        if(dir.isNotBlank()) {
            load(dir)
        }
    }

    fun selectDirectory() {
        val chooser = DirectoryChooser()
        chooser.initialDirectory = File("./")
        val directory = chooser.showDialog(null) ?: return
        load(directory.absolutePath)
    }

    private fun load(directory: String) {
        loaded = false

//        val path = CachePath(directory)
//
//        if (!path.isValid()) {
//            alert(Alert.AlertType.ERROR, "Error loading cache", "Invalid cache.")
//            return
//        }
        val cache: Cache = CacheDelegate(directory, "1", "1")

        try {
//            cache.setPath(path)
//
//            if (!cache.load()) {
//                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load cache files.")
//                return
//            }
//
            if (!sprites.load(cache)) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load sprite archive.")
                return
            }
//
            if (!interfaces.load(cache)) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load interface archive.")
                return
            }
//
//            if (!fonts.load(cache)) {
//                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load fontIndex archive.")
//                return
//            }
            Settings.put(Settings.CACHE_DIRECTORY, directory)
            loaded = true
        } catch (e: FileNotFoundException) {
            alert(Alert.AlertType.WARNING, "Error loading cache", "Cache already in use.")
        }
    }

    fun save(widgets: WidgetsController) {
        if(loaded) {

//            interfaces.save(widgets, cache)

            println("Save complete")
        }
    }

    fun unlink() {
        if(loaded) {
//            cache.reset()
            interfaces.reset()
            sprites.reset()
            fonts.reset()
            loaded = false
        }
        WidgetBuilder.identifier = 0
    }

}