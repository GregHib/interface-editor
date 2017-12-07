package com.greg.properties

interface Linkable {

    var links: MutableList<(value: Any?) -> Unit>

    fun refresh(value: Any?)

    fun link(action: (value: Any?) -> Unit)

}