package com.greg.controller.controller

import com.greg.controller.model.Widget
import com.greg.controller.model.WidgetRectangle
import com.greg.controller.model.WidgetText
import com.greg.ui.canvas.widget.type.WidgetType
import javafx.scene.paint.Color

class MementoBuilder(val widget: Widget) {
    fun build(): Memento {
        val memento = Memento(widget.type)
        widget.xProperty()

        memento.values.add(widget.getX())
        memento.values.add(widget.getY())
        memento.values.add(widget.getWidth())
        memento.values.add(widget.getHeight())


        if (widget is WidgetRectangle) {
            memento.values.add(widget.getColour())
        }

        if (widget is WidgetText) {
            memento.values.add(widget.getText())
            memento.values.add(widget.getColour())
        }

        return memento
    }

    companion object {
        fun convert(name: String, list: MutableList<String>): Memento {
            val type = WidgetType.valueOf(name)

                val memento = Memento(type)

                memento.values.add(list.removeAt(0).toInt())
                memento.values.add(list.removeAt(0).toInt())
                memento.values.add(list.removeAt(0).toInt())
                memento.values.add(list.removeAt(0).toInt())

                if(type == WidgetType.RECTANGLE) {
                    memento.values.add(Color.valueOf(list.removeAt(0)))
                } else if(type == WidgetType.TEXT) {
                    memento.values.add(list.removeAt(0))
                    memento.values.add(Color.valueOf(list.removeAt(0)))
                }
                return memento
        }
    }

}