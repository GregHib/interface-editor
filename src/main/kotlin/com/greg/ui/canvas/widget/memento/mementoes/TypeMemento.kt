package com.greg.ui.canvas.widget.memento.mementoes

open class TypeMemento(val type: String) : Memento() {

    override fun toString(): String {
        return "$type" + values.toString()
    }
}