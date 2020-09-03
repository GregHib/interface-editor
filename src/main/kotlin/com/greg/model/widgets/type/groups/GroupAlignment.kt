package com.greg.model.widgets.type.groups

import com.greg.controller.utils.MathUtils
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.properties.extended.BoolProperty
import com.greg.model.widgets.properties.extended.IntProperty
import com.greg.model.widgets.properties.extended.ObjProperty

interface GroupAlignment {

    var centred: BoolProperty

    fun setCentred(value: Boolean) {
        centred.set(value)
    }

    fun isCentred(): Boolean {
        return centred.get()
    }

}