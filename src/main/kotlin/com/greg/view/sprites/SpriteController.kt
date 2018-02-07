package com.greg.view.sprites

import com.greg.controller.utils.BSPUtils
import com.greg.controller.utils.Dialogue
import com.greg.model.Sprite
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.concurrent.Task
import javafx.scene.image.Image
import org.apache.commons.imaging.Imaging
import tornadofx.Controller
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.experimental.and

class SpriteController : Controller() {

    companion object {
        val placeholderIcon = Image(javaClass.getResourceAsStream("placeholder.png"))

        val observableList: ObservableList<Sprite> = FXCollections.observableArrayList()
        var filteredList = FilteredList(observableList, { _ -> true })
    }



    fun importBinary() {
//        val chooser = FileChooser()
//        chooser.initialDirectory = Paths.get(System.getProperty("user.home")).toFile()
//        chooser.extensionFilters.add(FileChooser.ExtensionFilter("main_file_sprites.dat", "*.dat"))
//        val selectedFile = chooser.showOpenDialog(primaryStage) ?: return
        val selectedFile = File("C:\\Users\\Greg\\.fury\\cache\\main_file_sprites.dat")
        importBinary(selectedFile)
    }

    fun importBinary(selectedFile: File) {

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
                            Platform.runLater({ observableList.add(Sprite(i, imageData, "png")) })
                        } else {

                            dataBuf.get(imageData)

                            val info = Imaging.getImageInfo(imageData)

                            val sprite = Sprite(i, imageData, info.format.name)
                            sprite.drawOffsetX = offsetX
                            sprite.drawOffsetY = offsetY

                            Platform.runLater({ observableList.add(sprite) })
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
}