package com.greg.widget

import com.greg.selection.DragModel
import javafx.scene.text.Text

class WidgetText(text: String?) : Text(text), Widget {
    override lateinit var drag: DragModel
}
