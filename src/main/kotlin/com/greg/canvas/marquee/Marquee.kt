package com.greg.canvas.marquee

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeLineCap

class Marquee : Rectangle() {

    private var mouseAnchorX: Double = 0.0
    private var mouseAnchorY: Double = 0.0
    var selecting = false

    init {
        stroke = Color.BLUE
        strokeWidth = 1.0
        strokeLineCap = StrokeLineCap.ROUND
        fill = Color.LIGHTBLUE.deriveColor(0.0, 1.2, 1.0, 0.6)
    }

    fun add(startX: Double, startY: Double) {
        mouseAnchorX = startX
        mouseAnchorY = startY

        x = mouseAnchorX
        y = mouseAnchorY
        width = 0.0
        height = 0.0
    }

    fun draw(drawX: Double, drawY: Double) {
        if(drawX > mouseAnchorX) {
            x = mouseAnchorX - 0.5
            width = drawX - mouseAnchorX
        } else {
            x = drawX + 0.5
            width = mouseAnchorX - drawX
        }

        if(drawY > mouseAnchorY) {
            y = mouseAnchorY - 0.5
            height = drawY - mouseAnchorY
        } else {
            y = drawY + 0.5
            height = mouseAnchorY - drawY
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