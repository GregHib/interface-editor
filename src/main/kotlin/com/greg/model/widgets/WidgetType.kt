package com.greg.model.widgets

import com.greg.model.widgets.type.*
enum class WidgetType(val type: String?, val resizable: Boolean = true, val hidden: Boolean = false) {
    CONTAINER(WidgetContainer::class.simpleName),
    MODEL_LIST(WidgetModelList::class.simpleName),
    INVENTORY(WidgetInventory::class.simpleName),
    RECTANGLE(WidgetRectangle::class.simpleName),
    TEXT(WidgetText::class.simpleName),
    SPRITE(WidgetSprite::class.simpleName, false),
    MODEL(WidgetModel::class.simpleName),
    ITEM_LIST(WidgetItemList::class.simpleName);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
    }
}

