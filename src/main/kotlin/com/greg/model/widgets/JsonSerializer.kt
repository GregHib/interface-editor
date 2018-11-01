package com.greg.model.widgets

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonSerializer {
    private val gson = Gson()

    fun serialize(any: Any): String {
        return gson.toJson(any)
    }

    fun <T : Any> deserializer(string: String, type: Class<T>): T? {
        return gson.getAdapter(TypeToken.get(type)).fromJson(string)
    }
}