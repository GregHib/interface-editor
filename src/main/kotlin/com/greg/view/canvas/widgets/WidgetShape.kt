package com.greg.view.canvas.widgets

import com.greg.model.settings.Settings
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import javafx.util.Duration
import tornadofx.add

open class WidgetShape(val identifier: Int, width: Int, height: Int) : Group() {
    val outline = Rectangle(0.0, 0.0, width.toDouble(), height.toDouble())

    init {
        add(outline)

        if(Settings.getBoolean(Settings.SELECTION_STROKE_ANIMATE)) {
            outline.strokeDashArray.setAll(4.0, 8.0)
            val maxOffset = outline.strokeDashArray.stream().reduce(0.0) { a, b -> a + b }
            val line = Timeline(KeyFrame(Duration.ZERO, KeyValue(outline.strokeDashOffsetProperty(), 0, Interpolator.LINEAR)), KeyFrame(Duration.seconds(Settings.getDouble(Settings.SELECTION_STROKE_ANIMATION_DURATION)), KeyValue(outline.strokeDashOffsetProperty(), maxOffset, Interpolator.LINEAR)))
            line.cycleCount = Timeline.INDEFINITE
            line.play()
        }

        outline.stroke = Settings.getColour(Settings.DEFAULT_STROKE_COLOUR)
        outline.fill = Color.TRANSPARENT
        outline.strokeType = StrokeType.INSIDE
    }

}