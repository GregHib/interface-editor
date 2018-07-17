package com.greg.model.cache.formats

import com.greg.model.cache.CacheFormat
import java.io.File

class FullCacheFormat : CacheFormat {

    override fun format(file: File, files: List<File>): Boolean {
        //Check has indices
        val index = files.firstOrNull { it.extension == "idx0" } ?: return false

        //cache identifier
        val identifier = index.nameWithoutExtension

        //has cache data file
        files.firstOrNull { it.nameWithoutExtension == identifier && it.extension == "dat" } ?: return false

        return file.isDirectory || file.nameWithoutExtension == identifier
    }

}