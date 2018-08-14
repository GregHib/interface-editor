package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupActions {

    var hasActions: BoolProperty?
    var actions: ObjProperty<Array<String?>>?

    fun setHasActions(value: Boolean) { hasActionsProperty().set(value) }

    fun hasActions(): Boolean { return hasActionsProperty().get() }

    fun hasActionsProperty(): BoolProperty {
        if (hasActions == null)
            hasActions = BoolProperty(this, "hasActions", false)

        return hasActions!!
    }

    fun setActions(value: Array<String?>) { actionsProperty().set(value) }

    fun getActions(): Array<String?> { return actionsProperty().get() }

    fun actionsProperty(): ObjProperty<Array<String?>> {
        if (actions == null)
            actions = ObjProperty(this, "actions", arrayOfNulls(0))

        return actions!!
    }
}