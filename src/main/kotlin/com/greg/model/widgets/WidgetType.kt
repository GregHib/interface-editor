package com.greg.model.widgets

import com.greg.model.widgets.type.*
import com.greg.view.canvas.widgets.*
import kotlin.reflect.KClass

enum class WidgetType(val widget: KClass<out Widget>, val shape: KClass<out WidgetShape>, val type: String? = widget.simpleName, val resizable: Boolean = true, val hidden: Boolean = false) {
    CONTAINER(WidgetContainer::class, ContainerShape::class),
    MODEL_LIST(WidgetModelList::class, ModelListShape::class),
    INVENTORY(WidgetInventory::class, InventoryShape::class, resizable = false),
    RECTANGLE(WidgetRectangle::class, RectangleShape::class),
    TEXT(WidgetText::class, TextShape::class),
    SPRITE(WidgetSprite::class, SpriteShape::class, resizable = false),
    MODEL(WidgetModel::class, ModelShape::class),
    ITEM_LIST(WidgetItemList::class, ItemListShape::class);

    companion object {
        fun forString(string: String?): WidgetType? {
            return values().firstOrNull { it.name == string }
        }
        fun forIndex(index: Int): WidgetType {
            return values().firstOrNull { it.ordinal == index } ?: CONTAINER
        }
    }
}

