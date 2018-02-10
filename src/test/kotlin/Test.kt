
import io.nshusa.rsam.FileStore
import io.nshusa.rsam.IndexedFileSystem
import io.nshusa.rsam.binary.Archive
import io.nshusa.rsam.binary.sprite.ImageArchive
import io.nshusa.rsam.util.HashUtils
import java.io.File
import java.io.InputStream
import java.nio.file.Paths

object Test {
    private val directory = "${System.getProperty("user.dir")}/dump/"

    fun run() {
        val start = System.currentTimeMillis()
        IndexedFileSystem.init(Paths.get("./cache/")).use { fs ->
            println(System.currentTimeMillis() - start)

            val store = fs.getStore(FileStore.ARCHIVE_FILE_STORE)

            val mediaArchive = Archive.decode(store.readFile(Archive.MEDIA_ARCHIVE))

            for(entry in mediaArchive.entries) {

                val imageArchive = ImageArchive.decode(mediaArchive, entry.hash)

                val decoded = getName(entry.hash)

                val name = decoded?.substring(0, decoded.length - 4) ?: entry.hash
                val file = File(directory + name)

                if(!file.exists())
                    file.mkdirs()

                for (i in 0 until imageArchive.sprites.size) {
                    val sprite = imageArchive.sprites[i]

//                    ImageIO.write(sprite.toBufferedImage(), "png", File(file, i.toString() + ".png"))
                }

                println(String.format("There are %d sprites in archive %s (%d)", imageArchive.sprites.size, name, imageArchive.hash))
            }
        }
    }

    fun getName(hash: Int): String? {
        val inputStream: InputStream = javaClass.getResourceAsStream("4.txt")
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        lineList.forEach{
            if(HashUtils.nameToHash(it) == hash)
                return it
        }
        return null
    }
}


fun main(args: Array<String>) {
    Test.run()
}