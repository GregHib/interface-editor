package com.greg.model.widgets

interface Jsonable {

    fun toJson(): String

    fun fromJson(json: String)

}