package com.greg.model.cache.formats

import java.io.File

interface CacheFormat {

    fun format(file: File, files: List<File>): Boolean

}