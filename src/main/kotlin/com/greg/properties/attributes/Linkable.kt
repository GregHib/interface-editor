package com.greg.properties.attributes

interface Linkable {

    var links: MutableList<(value: Any?) -> Unit>

    fun link(action: (value: Any?) -> Unit)

}