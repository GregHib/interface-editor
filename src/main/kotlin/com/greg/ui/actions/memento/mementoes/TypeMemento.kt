package com.greg.ui.actions.memento.mementoes

open class TypeMemento(val type: String) : Memento() {

    override fun toString(): String {
        return "$type" + values.toString()
    }
}