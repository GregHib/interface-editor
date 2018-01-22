package src.com.greg.view.widgets

import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import src.com.greg.view.WidgetShape
import tornadofx.add

class RectangleShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height) {

    val rectangle = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())

    init {
        add(rectangle)
        rectangle.strokeType = StrokeType.INSIDE
        rectangle.widthProperty().bind(outline.widthProperty())
        rectangle.heightProperty().bind(outline.heightProperty())
    }

}