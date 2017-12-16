package com.greg.canvas.state.edit.resize

import com.greg.canvas.widget.Widget
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.beans.binding.DoubleBinding
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class TabHandler {

    private val tabs = mutableListOf<ResizeTab>()

    fun init(widget: Widget) {
        val rect = widget.getRectangle().getNode() as Rectangle

        val x = widget.getNode().layoutXProperty()
        val y = widget.getNode().layoutYProperty()

        val offset = 0

        val tabWidth = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_WIDTH)
        val tabHeight = Settings.getDouble(SettingsKey.DEFAULT_WIDGET_MINIMUM_HEIGHT)

        val halfWidth = rect.widthProperty().divide(2.0).subtract(tabWidth / 2.0)
        val halfHeight = rect.heightProperty().divide(2.0).subtract(tabHeight / 2.0)

        //Calculate all positions for tabs to bind too
        val left = x.add(offset)
        val right = x.add(rect.widthProperty()).subtract(tabWidth).subtract(offset)
        val top = y.add(offset)
        val bottom = y.add(rect.heightProperty()).subtract(tabHeight).subtract(offset)
        val centreX = x.add(halfWidth)
        val centreY = y.add(halfHeight)

        //Add tabs for all directions
        for(dir in ResizeDirection.values())
            addTab(dir, right, left, top, bottom, centreX, centreY)
    }

    private fun addTab(dir: ResizeDirection, right: DoubleBinding, left: DoubleBinding, top: DoubleBinding, bottom: DoubleBinding, centreX: DoubleBinding, centreY: DoubleBinding) {
        val dirs = dir.directions
        val x = if (dirs.contains(Direction.EAST)) right else if (dirs.contains(Direction.WEST)) left else centreX
        val y = if (dirs.contains(Direction.NORTH)) top else if (dirs.contains(Direction.SOUTH)) bottom else centreY

        val tab = createTab(x, y)
        tab.addCursor(dir.cursor)
        tabs.add(tab)
    }

    private fun createTab(x: DoubleBinding?, y: DoubleBinding?): ResizeTab {
        val tab = ResizeTab(8.0, 8.0)
        tab.fill = Color.WHITE
        tab.xProperty().bind(x)
        tab.yProperty().bind(y)
        return tab
    }

    fun get(): List<ResizeTab> {
        return tabs
    }

    fun close() {
        tabs.clear()
    }

    fun indexOf(target: ResizeTab): Int {
        return tabs.indexOf(target)
    }
}