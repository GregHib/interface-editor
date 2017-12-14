package com.greg.canvas.widget

import com.greg.canvas.DragModel
import javafx.scene.Node
import javafx.scene.paint.Color

abstract class WidgetData: AttributeWidget {

    override fun getNode(): Node {
        return this
    }

    var components = mutableListOf<WidgetInterface>()
    var drag: DragModel? = null

    constructor(builder: WidgetBuilder) {
        //Add all the rest, default just rectangle
        for (component in builder.components)
            add(component)
    }

    protected fun addToStart(component: WidgetInterface) {
        components.add(0, component)
    }

    protected fun add(component: AttributeWidget) {
        components.add(component)
        children.add(component.getNode())
    }

    private fun setWidth(width: Double) {
        getRectangle().getRectangle()?.width = width
    }

    private fun setHeight(height: Double) {
        getRectangle().getRectangle()?.height = height
    }

    fun setSelection(colour: Color?) {
        getRectangle().getRectangle()?.stroke = colour
    }

    fun getRectangle(): WidgetRectangle {
        return components[1] as WidgetRectangle
    }

}