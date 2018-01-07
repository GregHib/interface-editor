package com.greg.ui.canvas.widget.builder.data

import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import com.greg.ui.canvas.movement.StartPoint
import com.greg.ui.canvas.widget.Widget
import com.greg.ui.canvas.widget.builder.WidgetBuilder
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import javafx.scene.Node
import javafx.scene.paint.Color

abstract class WidgetData(builder: WidgetBuilder, id: Int) : WidgetFacade() {

    var components = mutableListOf<Widget>()
    var start: StartPoint? = null
    var identifier: Int = id

    init {
        //Add all the rest, default just rectangle
        for (component in builder.components)
            add(component)
    }

    override fun getNode(): Node {
        return this
    }

    protected fun addToStart(component: Widget) {
        components.add(0, component)
    }

    protected fun add(component: WidgetFacade) {
        components.add(component)
        children.add(component.getNode())
    }

    fun setWidth(width: Double) {
        getRectangle().getRectangle().width = width
    }

    fun setHeight(height: Double) {
        getRectangle().getRectangle().height = height
    }

    fun setSelection(colour: Color?) {
        getRectangle().getRectangle().stroke = colour
    }

    fun getRectangle(): WidgetRectangle {
        return components[1] as WidgetRectangle
    }

    fun getMinimumWidth(): Double {
        return Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_WIDTH)
    }

    fun getMinimumHeight(): Double {
        return Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_HEIGHT)
    }
}