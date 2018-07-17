package com.greg.model.cache

import java.io.File


class CachePath(val file: File) {

    /**
     * Checks the path is a valid accessible interface file or cache
     */
    fun isValid() : Boolean {
        return file.canRead() && (isInterfaceFile() || checkCacheFiles())
    }


    fun isInterfaceFile() : Boolean {
        return file.isFile && file.nameWithoutExtension == "interface" && file.extension == "jag"//TODO replace 'interface' name with format check
    }

    var identifier = "main_file_cache"
    var data: File? = null
    var indices: List<File>? = null

    /**
     * Checks the path is a directory and contains the minimum files required for a valid cache
     * Records the cache identifier & number of indices to local variables
     */
    private fun checkCacheFiles() : Boolean {
        if(!file.isDirectory)
            return false

        val files = getFiles()

        //has first index file
        val index = files.firstOrNull { it.extension == "idx0" } ?: return false

        //cache identifier
        identifier = index.nameWithoutExtension

        //has cache data file
        data = files.firstOrNull { it.nameWithoutExtension == identifier && it.extension == "dat" } ?: return false

        //collect index files
        indices = files.filter { it.nameWithoutExtension == identifier && it.extension.startsWith("idx") }.toList()

        return true
    }

    /**
     * Returns all file in path directory
     * @return List of files
     */
    fun getFiles(): List<File> {
        return file.listFiles().filter { it.isFile }
    }

    override fun equals(other: Any?): Boolean {
        if(other is CachePath)
            return file == other.file
        return super.equals(other)
    }

    override fun toString(): String {
        return file.toString()
    }

    override fun hashCode(): Int {
        var result = file.hashCode()
        result = 31 * result + identifier.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (indices?.hashCode() ?: 0)
        return result
    }
}