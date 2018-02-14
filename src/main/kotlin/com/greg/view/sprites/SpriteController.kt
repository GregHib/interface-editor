package com.greg.view.sprites

import com.greg.controller.utils.BSPUtils
import com.greg.controller.utils.Dialogue
import com.greg.view.sprites.external.ExternalSprite
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.Sprite
import io.nshusa.rsam.util.HashUtils
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.concurrent.Task
import javafx.scene.image.Image
import tornadofx.Controller
import tornadofx.observable
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.experimental.and

class SpriteController : Controller() {

    companion object {
        val placeholderIcon = Image(javaClass.getResourceAsStream("placeholder.png"))

        private val observableExternal: ObservableList<ExternalSprite> = FXCollections.observableArrayList()
        var filteredExternal = FilteredList(observableExternal, { _ -> true })

        private var observableInternal: ObservableList<ImageArchive> = FXCollections.observableArrayList()
        var filteredInternal = FilteredList(observableInternal, { _ -> true })

        fun getArchive(archive: String): ImageArchive? {
            return filteredInternal.firstOrNull { it.hash == HashUtils.nameToHash(archive) }
        }
    }

    fun getInternalArchiveNames(): List<String> {
        return filteredInternal
                .map { it -> getName(it.hash) }
                .map { it.substring(0, it.length - 4) }
    }

    fun importBinary() {
//        val chooser = FileChooser()
//        chooser.initialDirectory = Paths.get(System.getProperty("user.home")).toFile()
//        chooser.extensionFilters.add(FileChooser.ExtensionFilter("main_file_sprites.dat", "*.dat"))
//        val selectedFile = chooser.showOpenDialog(primaryStage) ?: return
        val selectedFile = File("C:\\Users\\Greg\\.fury\\cache\\main_file_sprites.dat")
        importBinary(selectedFile)
    }

    private fun importBinary(selectedFile: File) {

        if (selectedFile.length() < 3) {
            return
        }

        val prefix = BSPUtils.getFilePrefix(selectedFile)

        val metaFile = File(selectedFile.parent, "$prefix.idx")

        if (!metaFile.exists()) {
            Dialogue.showWarning("Could not locate corresponding idx file=${metaFile.name}").showAndWait()
            return
        }

        val task: Task<Boolean> = object : Task<Boolean>() {

            override fun call(): Boolean {
                FileChannel.open(selectedFile.toPath(), StandardOpenOption.READ).use { dat ->
                    val signature = ByteBuffer.allocate(3)

                    dat.read(signature)

                    if (signature[0].toChar() != 'b' && signature[1].toChar() != 's' && signature[2].toChar() != 'p') {
                        Platform.runLater({ Dialogue.showWarning("Detected invalid file format.").showAndWait() })
                        return false
                    }

                }

                val dataBuf = ByteBuffer.wrap(Files.readAllBytes(selectedFile.toPath()))
                dataBuf.position(3)

                val metaBuf = ByteBuffer.wrap(Files.readAllBytes(metaFile.toPath()))

                val entries = metaBuf.capacity() / 10

                for (i in 0 until entries) {
                    try {
                        val dataOffset = ((metaBuf.get().toInt() and 0xFF) shl 16) + ((metaBuf.get().toInt() and 0xFF) shl 8) + (metaBuf.get().toInt() and 0xFF)
                        val length = ((metaBuf.get().toInt() and 0xFF) shl 16) + ((metaBuf.get().toInt() and 0xFF) shl 8) + (metaBuf.get().toInt() and 0xFF)
                        val offsetX = (metaBuf.short and 0xFF).toInt()
                        val offsetY = (metaBuf.short and 0xFF).toInt()

                        dataBuf.position(dataOffset)

                        val imageData = ByteArray(length)

                        if (length == 0) {
                            Platform.runLater({ observableExternal.add(ExternalSprite(i, imageData)) })
                        } else {

                            dataBuf.get(imageData)

                            Platform.runLater({
                                val container = ExternalSprite(i, imageData)
                                container.sprite.offsetX = offsetX
                                container.sprite.offsetY = offsetY
                                observableExternal.add(container)
                            })
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        Platform.runLater({ Dialogue.showWarning("Detected corrupt file or invalid format.").showAndWait() })
                        return false
                    }

                }
                return true
            }
        }

        Thread(task).start()
    }

    fun start() {
        importBinary()
        val fs = IndexedFileSystem.init(Paths.get("./cache/"))
        fs.load()
        importCache(fs)
    }

    fun importCache(fs: IndexedFileSystem) {
        val task: Task<Boolean> = object : Task<Boolean>() {

            override fun call(): Boolean {
                try {
                    val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)

                    val mediaArchive = Archive.decode(store.readFile(Archive.MEDIA_ARCHIVE))

                    for(entry in mediaArchive.entries) {
                        val sprites = mutableListOf<Sprite>()
                        var index = 0
                        while (true) {
                            try {
                                val sprite = Sprite.decode(mediaArchive, entry.hash, index)
                                sprites.add(sprite)
                                index++
                            } catch (ex: Exception) {
                                break
                            }
                        }

                        if(sprites.size > 0)
                            observableInternal.add(ImageArchive(entry.hash, sprites))

//                        println(String.format("There are %d sprites in archive %s", index, entry.hash))
                    }

                    val sortedList = observableInternal.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { getName(it.hash) }))
                    observableInternal.clear()
                    observableInternal.addAll(sortedList)

                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                return true
            }
        }
        Thread(task).start()
    }

    fun getName(hash: Int): String {
        val inputStream: InputStream = javaClass.getResourceAsStream("4.txt")
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) } }
        lineList.forEach {
            if (HashUtils.nameToHash(it) == hash)
                return it
        }
        return hash.toString()
    }
}
