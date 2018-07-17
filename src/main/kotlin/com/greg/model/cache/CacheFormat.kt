package com.greg.model.cache

import java.io.File

interface CacheFormat {

    fun format(file: File, files: List<File>): Boolean

}