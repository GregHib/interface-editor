package com.greg.properties.attributes

interface PropertyInterface {

    var actions: MutableList<(value: Any?) -> Unit>//TODO what if another type is

    fun link(action: (value: Any?) -> Unit)

}