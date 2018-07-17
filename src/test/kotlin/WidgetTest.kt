
import java.io.IOException

object WidgetTest {
    fun run() {
        try {
            /*IndexedFileSystem(Paths.get("./cache/")).use { fs ->
                val archiveStore = fs.getStore(FileStore.ARCHIVE_FILE_STORE)

                val widgetArchive = Archive.decode(archiveStore.readFile(Archive.INTERFACE_ARCHIVE))
                val graphicArchive = Archive.decode(archiveStore.readFile(Archive.MEDIA_ARCHIVE))
                val fontArchive = Archive.decode(archiveStore.readFile(Archive.TITLE_ARCHIVE))

                val smallFont = Font(fontArchive, "p11_full", false)
                val frameFont = Font(fontArchive, "p12_full", false)
                val boldFont = Font(fontArchive, "b12_full", false)
                val font2 = Font(fontArchive, "q8_full", true)

                val fonts = arrayOf<Font>(smallFont, frameFont, boldFont, font2)
                Widget.load(widgetArchive, graphicArchive, fonts)

                println(String.format("There are %s widgets.", Widget.widgets.length))

            }*/
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

fun main(args: Array<String>) {
    Test.run()
}