package com.greg.controller.canvas

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
internal class SceneGestures(private var canvas: PannableCanvas) {

    private val sceneDragContext = DragContext()
    private var spacePressed = false

    // right mouse button => panning
    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!spacePressed)
            return@EventHandler

        sceneDragContext.mouseAnchorX = event.sceneX.toInt()
        sceneDragContext.mouseAnchorY = event.sceneY.toInt()

        sceneDragContext.anchorX = canvas.translateX.toInt()
        sceneDragContext.anchorY = canvas.translateY.toInt()

        event.consume()
    }

    // right mouse button => panning
    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!spacePressed)
            return@EventHandler

        canvas.translateX = (sceneDragContext.anchorX + event.sceneX - sceneDragContext.mouseAnchorX).toInt().toDouble()
        canvas.translateY = (sceneDragContext.anchorY + event.sceneY - sceneDragContext.mouseAnchorY).toInt().toDouble()

        event.consume()
    }

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    // currently we only use Y, same value is used for X
    // note: pivot value must be untransformed, i. e. without scaling
    val onScrollEventHandler: EventHandler<ScrollEvent> = EventHandler { event ->
        val delta = 1.2

        var scale = canvas.scale
        val oldScale = scale

        if (event.deltaY < 0)
            scale /= delta
        else
            scale *= delta

        scale = clamp(scale, MIN_SCALE, MAX_SCALE)

        val f = scale / oldScale - 1

        val dx = event.x - (canvas.boundsInParent.width / 2 + canvas.boundsInParent.minX)
        val dy = event.y - (canvas.boundsInParent.height / 2 + canvas.boundsInParent.minY)

        canvas.scale = scale
        canvas.setPivot(f * dx, f * dy)

        event.consume()
    }

    val onKeyPressedEventHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.SPACE && !spacePressed) {
            spacePressed = true
            event.consume()
        }
    }

    val onKeyReleasedEventHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.SPACE && spacePressed) {
            spacePressed = false
            event.consume()
        }
    }

    companion object {

        private const val MAX_SCALE = 10.0
        private const val MIN_SCALE = .5


        fun clamp(value: Double, min: Double, max: Double): Double {

            if (java.lang.Double.compare(value, min) < 0)
                return min

            return if (java.lang.Double.compare(value, max) > 0) max else value

        }
    }
}