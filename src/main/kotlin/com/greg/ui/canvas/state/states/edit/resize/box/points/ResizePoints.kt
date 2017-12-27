package com.greg.ui.canvas.state.states.edit.resize.box.points

import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.beans.binding.DoubleBinding
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class ResizePoints {

    private val points = mutableListOf<ResizePoint>()

    fun init(widget: WidgetGroup) {
        val rect = widget.getRectangle().getNode() as Rectangle

        val x = widget.getNode().layoutXProperty()
        val y = widget.getNode().layoutYProperty()

        val offset = 0

        val width = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_WIDTH)
        val height = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_HEIGHT)

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

        val point = createPoint(x, y)
        point.addCursor(direction.cursor)
        points.add(point)
    }

    private fun createPoint(x: DoubleBinding?, y: DoubleBinding?): ResizePoint {
        val point = ResizePoint(8.0, 8.0)
        point.fill = Color.WHITE
        point.xProperty().bind(x)
        point.yProperty().bind(y)
        return point
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