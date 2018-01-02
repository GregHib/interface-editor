package com.greg.ui.action.containers

import com.greg.ui.action.Action
import com.greg.ui.action.change.Change

class Actions : ActionList() {

    private var action: Action? = null

    fun start() {
        if (action == null) {
            action = Action()
        }
    }

    fun finish(): Boolean {
        //Greater than 1 because a starting "change" is always added
        var hasActions = action != null && action!!.size() > 1
        if (hasActions)
            add(action!!)
        action = null
        return hasActions
    }

    fun record(change: Change) {
        if (action == null)
            add(Action(change))
        else
            action!!.add(change)
    }
}