package com.greg.model.cache

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.archives.ArchiveFont
import com.greg.model.cache.archives.ArchiveInterface
import com.greg.model.cache.archives.ArchiveMedia
import javafx.scene.control.Alert
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import tornadofx.Controller
import tornadofx.alert
import java.io.File
import java.io.FileNotFoundException

class CacheController : Controller() {

    var path: CachePath? = null

    var cache: Cache? = null

    val fonts = ArchiveFont()
    val sprites = ArchiveMedia()
    val interfaces = ArchiveInterface()

    var loaded = false

    fun selectDirectory() {
        val chooser = DirectoryChooser()
        chooser.initialDirectory = File("./")
        val directory = chooser.showDialog(null) ?: return
        select(directory)
    }

    fun selectFile() {
        val chooser = FileChooser()
        chooser.initialDirectory = File("./")
        chooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Jagex Archive File (*.jag)", "*.jag"),
                FileChooser.ExtensionFilter("Cache Data File (*.dat)", "*.dat")
        )

        val directory = chooser.showOpenDialog(null) ?: return
        select(directory)
    }

    private fun select(directory: File) {
        loaded = false

        val path = CachePath(directory)

        if (!path.isValid()) {
            alert(Alert.AlertType.ERROR, "Error loading cache", "Invalid cache.")
            return
        }

        try {
            val cache = Cache(path)

            if (!cache.load()) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load cache files.")
                return
            }

            if (!sprites.load(cache)) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load sprite archive.")
                return
            }

            if (!interfaces.load(cache)) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load interface archive.")
                return
            }

            if (!fonts.load(cache)) {
                alert(Alert.AlertType.ERROR, "Error loading cache", "Unable to load fontIndex archive.")
                return
            }

            this.path = path
            this.cache = cache

            loaded = true
        } catch (e: FileNotFoundException) {
            alert(Alert.AlertType.WARNING, "Error loading cache", "Cache already in use.")
        }
    }

    fun save(widgets: WidgetsController) {
        if(loaded && cache != null) {

            interfaces.save(widgets, cache!!)

            println("Save complete")
        }
    }

}