package com.greg.view.canvas.states.edit

import com.greg.model.settings.Settings
import javafx.beans.binding.DoubleBinding
import javafx.scene.Cursor
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType

class ResizePoint(x: DoubleBinding?, y: DoubleBinding?, cursor: Cursor) : Rectangle(Settings.getDouble(Settings.DEFAULT_WIDGET_RESIZE_TAB_WIDTH), Settings.getDouble(Settings.DEFAULT_WIDGET_RESIZE_TAB_HEIGHT)) {

    init {
        //Bind to correct spot
        xProperty().bind(x)
        yProperty().bind(y)

        //Set cursor type
        this.cursor = cursor

        strokeType = StrokeType.INSIDE
        fill = Color.WHITE
    }

}