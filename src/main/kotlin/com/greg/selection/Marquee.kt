package com.greg.selection

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap

class Marquee: Rectangle() {

    private var mouseAnchorX: Double = 0.0
    private var mouseAnchorY: Double = 0.0
    var selecting = false

    init {
        stroke = Color.BLUE
        strokeWidth = 1.0
        strokeLineCap = StrokeLineCap.ROUND
        fill = Color.LIGHTBLUE.deriveColor(0.0, 1.2, 1.0, 0.6)
    }

    fun add(x: Double, y: Double) {
        mouseAnchorX = x
        mouseAnchorY = y

        this.x = mouseAnchorX
        this.y = mouseAnchorY
        width = 0.0
        height = 0.0
    }

    fun draw(x: Double, y: Double) {
        val offsetX = x - mouseAnchorX
        val offsetY = y - mouseAnchorY

        if (offsetX > 0)
            width = offsetX
        else {
            this.x = x + 0.5
            width = mouseAnchorX - this.x + 0.5
        }

        if (offsetY > 0) {
            height = offsetY
        } else {
            this.y = y + 0.5
            height = mouseAnchorY - this.y + 0.5
        }
    }

    fun reset() {
        x = 0.0
        y = 0.0
        width = 0.0
        height = 0.0
        selecting = false
    }
}