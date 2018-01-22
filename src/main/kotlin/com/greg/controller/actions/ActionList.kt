package com.greg.controller.actions

open class ActionList {

    private val list = mutableListOf<Action>()

    fun add(action: Action?) {
        if(action != null)
            list.add(action)
    }

    fun isNotEmpty(): Boolean {
        return list.isNotEmpty()
    }

    fun last(): Action {
        return list.last()
    }

    fun remove(action: Action) {
        list.remove(action)
    }

    fun clear() {
        list.clear()
    }
}