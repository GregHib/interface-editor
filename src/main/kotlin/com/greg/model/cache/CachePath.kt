package com.greg.model.cache

import com.greg.model.cache.formats.UnpackedFormat
import java.io.File
import java.nio.file.Path


class CachePath {
    private val path: File

    constructor(path: File) {
        this.path = path
    }

    constructor(path: String) : this(File(path))

    constructor(path: Path) : this(path.toFile())

    /**
     * Checks the path is a valid format
     */
    fun isValid() : Boolean {
        if(!path.exists())
            return false

        val files = getFiles()

        return CacheTypes.values().any { it.type.format(path, files) }
    }

    /**
     * @return cache format type
     */
    fun getCacheType(): CacheTypes {
        val files = getFiles()

        return CacheTypes.values().first { it.type.format(path, files) }
    }

    /**
     * @return the cache name identifier, default is "main_file_cache"
     */
    private fun getIdentifier(files: List<File>): String? {
        val index = files.firstOrNull { it.extension == "idx0" } ?: return null

        return index.nameWithoutExtension
    }

    /**
     * @return main file cache .dat file
     */
    fun getDataFile(files: List<File>): File? {

        val identifier = getIdentifier(files)

        return files.firstOrNull { it.nameWithoutExtension == identifier && it.extension == "dat" }
    }

    /**
     * @return list of all the cache index files
     */
    fun getIndices(files: List<File>): List<File> {

        val identifier = getIdentifier(files)

        return files.filter { it.nameWithoutExtension == identifier && it.extension.startsWith("idx") }.toList()
    }

    /**
     * Checks directory for an unpacked archive file
     * @throws NullPointerException if file not found
     */
    fun getArchiveFile(files: List<File>, archive: Int): File {
        return files.firstOrNull { UnpackedFormat.isFile(it, UnpackedFormat.list[archive]) } ?: throw NullPointerException("Cannot find unpacked cache file '${UnpackedFormat.list[archive]}.jag'")
    }

    /**
     * Returns all files in the directory
     * @return List of files
     */
    fun getFiles(): List<File> {
        val directory = if(path.isFile) path.parentFile else path
        return directory.listFiles().filter { it.isFile }
    }

    /**
     * Misc overrides
     */

    override fun equals(other: Any?): Boolean {
        if(other is CachePath)
            return path == other.path
        return super.equals(other)
    }

    override fun toString(): String {
        return path.toString()
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}