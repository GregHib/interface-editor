package tests

import com.greg.model.cache.CachePath
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Font
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.TreeItem
import org.junit.Assert
import org.junit.Test
import java.io.File

class CacheInterfaceLoadingTest {

    private val path = CachePath(File("./cache/"))

    @Test
    fun loadFonts() {
        IndexedFileSystem(path).use { fs ->
            fs.load()
            val file = fs.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.TITLE_ARCHIVE)
            val archive = Archive.decode(file!!)

            val smallFont = Font.decode(archive, "p11_full", false)
            val frameFont = Font.decode(archive, "p12_full", false)
            val boldFont = Font.decode(archive, "b12_full", false)
            val font2 = Font.decode(archive, "q8_full", true)

            val fonts = arrayOf(smallFont, frameFont, boldFont, font2)
        }
    }

    @Test
    fun loadInterface() {
        IndexedFileSystem(path).use {
            it.load()

            val file = it.readFile(FileStore.ARCHIVE_FILE_STORE, Archive.INTERFACE_ARCHIVE)
            val widgetArchive = Archive.decode(file!!)

            Widget.decode(widgetArchive, null, null)

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

//                Platform.runLater {treeTableView.root.children.add(treeItem)}
            }
            Assert.assertTrue(Widget.count() > 0)

        }
    }

}