package tests

import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.Font
import io.nshusa.rsam.binary.Widget
import javafx.scene.control.TreeItem
import org.junit.Test
import java.nio.file.Paths

class CacheInterfaceLoadingTest {

    private val path = Paths.get("./cache/")

    @Test
    fun loadFonts() {
        IndexedFileSystem.init(path).use { fs ->
            fs.load()
            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)
            val archive = Archive.decode(store!!.readFile(Archive.TITLE_ARCHIVE)!!)

            val smallFont = Font.decode(archive, "p11_full", false)
            val frameFont = Font.decode(archive, "p12_full", false)
            val boldFont = Font.decode(archive, "b12_full", false)
            val font2 = Font.decode(archive, "q8_full", true)

            val fonts = arrayOf(smallFont, frameFont, boldFont, font2)
        }
    }

    @Test
    fun loadInterface() {
        IndexedFileSystem.init(path).use {
            it.load()

            val archiveStore = it.getStore(FileStore.ARCHIVE_FILE_STORE)
            val widgetArchive = Archive.decode(archiveStore!!.readFile(Archive.INTERFACE_ARCHIVE)!!)

            Widget.decode(widgetArchive, null, null)

            println(Widget.count())
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

        }
    }

}