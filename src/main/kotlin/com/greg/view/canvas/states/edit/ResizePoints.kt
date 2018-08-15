package com.greg.view.canvas.states.edit

import com.greg.controller.widgets.WidgetsController
import com.greg.model.settings.Settings
import com.greg.view.canvas.widgets.WidgetShape
import javafx.beans.binding.DoubleBinding

class ResizePoints {

    private val points = mutableListOf<ResizePoint>()

    fun init(widget: WidgetShape, widgets: WidgetsController) {
        val rect = widget.outline

        val translateX = widget.translateXProperty()
        val translateY = widget.translateYProperty()

        val point = widgets.getParentPosition(widget)

        val x = translateX.add(point.x - widget.translateX)
        val y = translateY.add(point.y - widget.translateY)

        val offset = 0.0

        val width = Settings.getDouble(Settings.DEFAULT_WIDGET_RESIZE_TAB_WIDTH)
        val height = Settings.getDouble(Settings.DEFAULT_WIDGET_RESIZE_TAB_HEIGHT)

        val halfWidth = rect.widthProperty().divide(2.0).subtract(width / 2.0)
        val halfHeight = rect.heightProperty().divide(2.0).subtract(height / 2.0)

        //Calculate all positions for points to bind too
        val left = x.add(offset)
        val right = x.add(rect.widthProperty()).subtract(width).subtract(offset)
        val top = y.add(offset)
        val bottom = y.add(rect.heightProperty()).subtract(height).subtract(offset)
        val centreX = x.add(halfWidth)
        val centreY = y.add(halfHeight)

        //Add points for all directions
        for(dir in Directions.values())
            addPoint(dir, right, left, top, bottom, centreX, centreY)
    }

    private fun addPoint(direction: Directions, right: DoubleBinding, left: DoubleBinding, top: DoubleBinding, bottom: DoubleBinding, centreX: DoubleBinding, centreY: DoubleBinding) {
        val directions = direction.directions
        val x = if (directions.contains(Direction.EAST)) right else if (directions.contains(Direction.WEST)) left else centreX
        val y = if (directions.contains(Direction.NORTH)) top else if (directions.contains(Direction.SOUTH)) bottom else centreY

        points.add(ResizePoint(x, y, direction.cursor))
    }

    fun get(): List<ResizePoint> {
        return points
    }

    fun close() {
        points.clear()
    }

    fun indexOf(target: ResizePoint): Int {
        return points.indexOf(target)
    }
}