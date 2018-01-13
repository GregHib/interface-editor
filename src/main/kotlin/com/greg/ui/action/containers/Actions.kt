package com.greg.ui.action.containers

import com.greg.ui.action.Action
import com.greg.ui.action.change.Change

class Actions : ActionList() {

    private var action: Action? = null

    fun start() {
        if (!started())
            action = Action()
    }

    fun started(): Boolean {
        return action != null
    }

    fun hasActions(): Boolean {
        //Greater than 1 because a starting "change" is always added
        return started() && action!!.size() > 1
    }

    fun finish(): Boolean {
        var actions = hasActions()
        if (actions)
            add(action!!)
        action = null
        return actions
    }

    fun record(change: Change) {
        if (action == null)
            add(Action(change))
        else
            action!!.add(change)
    }
}