package com.greg.model.cache.formats

import com.greg.model.cache.CacheFormat
import java.io.File

class UnpackedFormat : CacheFormat {

    override fun format(file: File, files: List<File>): Boolean {

        if(file.isFile && isUnpackedFile(file))
            return true

        if(file.isDirectory)
            return files.any { isUnpackedFile(it) }

        return false
    }

    private fun isUnpackedFile(file: File): Boolean {
        return list.any { isFile(file, it)}
    }

    companion object {
        val list = arrayOf("title", "config", "interface", "media", "versionlist", "textures", "wordenc", "sounds")


        fun isFile(file: File, name: String): Boolean {
            if(file.extension != "jag")
                return false

            return file.nameWithoutExtension == name
        }
    }
}