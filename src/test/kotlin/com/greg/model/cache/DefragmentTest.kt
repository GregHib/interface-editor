package com.greg.model.cache

import java.io.File
import java.io.IOException
import java.util.concurrent.CompletableFuture

class DefragmentTest {
    val cache = OldCache(CachePath("./cache/"))
    fun call() {
        try {
            val dir = File("./defragmented_cache/")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val dataFile = File(dir, "main_file_cache.dat")
            if (!dataFile.exists()) {
                dataFile.createNewFile()
            }

            for (i in 0 until cache.storeCount) {
                val idxFile = File(dir, "main_file_cache.idx$i")
                if (!idxFile.exists())
                    idxFile.createNewFile()
            }


            val nFs = OldCache(CachePath(dir.toPath()))

            var var26: Throwable? = null
            try {
                val stores = cache.storeCount

                for (i in 0 until cache.storeCount) {
                    val store = cache.getStore(i)
                    val copy = nFs.getStore(i)

                    val files = store.fileCount

                    for (file in 0..files) {
                        val buffer = store.readFile(file)
                        copy.writeFile(file, buffer?.array() ?: ByteArray(0))
                        val progress = i + 1 / stores * 100.0
                    }
                }
            } catch (t: Throwable) {
                var26 = t
                throw t
            } finally {
                if (nFs != null) {
                    if (var26 != null) {
                        try {
                            nFs.close()
                        } catch (var21: Throwable) {
                            var26.addSuppressed(var21)
                        }
                    } else {
                        nFs.close()
                    }
                }

            }
        } catch (var24: IOException) {
//            Platform.runLater(() -> {
//                Dialogue.showException("Error while defragmenting cache.", var24).showAndWait();
//            });
        }
    }
}

class example: CompletableFuture.AsynchronousCompletionTask
fun main(args: Array<String>) {
    val test = DefragmentTest()
    test.call()

}