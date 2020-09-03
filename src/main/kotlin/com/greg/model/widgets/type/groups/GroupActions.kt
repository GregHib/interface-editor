package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupActions {

    var hasActions: BoolProperty
    var actions: ObjProperty<Array<String>>

    fun setHasActions(value: Boolean) { hasActions.set(value) }

    fun hasActions(): Boolean { return hasActions.get() }

    fun setActions(value: Array<String>) { actions.set(value) }

    fun getActions(): Array<String> { return actions.get() }
}