package com.greg.ui.panel.panels.element

interface Element {

    var links: MutableList<(value: Any?) -> Unit>

    fun refresh(value: Any?)

    fun link(action: (value: Any?) -> Unit) {
        this.links.add(action)
    }

}