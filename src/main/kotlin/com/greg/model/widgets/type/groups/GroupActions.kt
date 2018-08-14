package com.greg.model.widgets.type.groups

import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.ObjProperty
import com.sun.xml.internal.fastinfoset.util.StringArray

interface GroupActions {

    var hasActions: BoolProperty?
    var actions: ObjProperty<StringArray>?

    fun setHasActions(value: Boolean) { hasActionsProperty().set(value) }

    fun hasActions(): Boolean { return hasActionsProperty().get() }

    fun hasActionsProperty(): BoolProperty {
        if (hasActions == null)
            hasActions = BoolProperty(this, "hasActions", false)

        return hasActions!!
    }

    fun setActions(value: StringArray) { actionsProperty().set(value) }

    fun getActions(): StringArray { return actionsProperty().get() }

    fun actionsProperty(): ObjProperty<StringArray> {
        if (actions == null)
            actions = ObjProperty(this, "actions", StringArray())

        return actions!!
    }
}