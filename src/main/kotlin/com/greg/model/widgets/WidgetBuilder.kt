package com.greg.model.widgets

import com.greg.model.widgets.type.*

open class WidgetBuilder(val type: WidgetType) {

    companion object {
        var identifier = 0

        fun getId() : Int {
            return identifier++
        }
    }

    open fun build(id: Int = -1): Widget {
        if(id != -1 && id > identifier)
            identifier = id + 1

        val identifier = if(id != -1) id else getId()
        return when(type) {
            WidgetType.CONTAINER -> WidgetContainer(this, identifier)
            WidgetType.MODEL_LIST -> WidgetModelList(this, identifier)
            WidgetType.INVENTORY -> WidgetInventory(this, identifier)
            WidgetType.RECTANGLE -> WidgetRectangle(this, identifier)
            WidgetType.TEXT -> WidgetText(this, identifier)
            WidgetType.SPRITE -> WidgetSprite(this, identifier)
            WidgetType.MODEL -> WidgetModel(this, identifier)
            WidgetType.ITEM_LIST -> WidgetItemList(this, identifier)
        }
    }
}