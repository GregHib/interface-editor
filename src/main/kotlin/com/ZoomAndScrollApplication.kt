package com

import javafx.application.Application
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import tornadofx.add

/**
 * The canvas which holds all of the nodes of the application.
 */
internal class PannableCanvas : Pane() {

    var myScale: DoubleProperty = SimpleDoubleProperty(1.0)

    /**
     * Set x/y scale
     * @param myScale
     */
    var scale: Double
        get() = myScale.get()
        set(scale) = myScale.set(scale)

    init {

        setPrefSize(600.0, 600.0)
        style = "-fx-background-color: lightgrey; -fx-border-color: blue;"

        // add scale transform
        scaleXProperty().bind(myScale)
        scaleYProperty().bind(myScale)

        // logging
        addEventFilter(MouseEvent.MOUSE_PRESSED) { event ->
            println(
                    "canvas event: " + (((event.sceneX - boundsInParent.minX) / scale).toString() + ", scale: " + scale)
            )
            println("canvas bounds: " + boundsInParent)
        }

    }

    /**
     * Add a grid to the canvas, send it to back
     */
    fun addGrid() {

        val w = boundsInLocal.width
        val h = boundsInLocal.height

        // add grid
        val grid = Canvas(w, h)

        // don't catch mouse events
        grid.isMouseTransparent = true

        val gc = grid.graphicsContext2D

        gc.stroke = Color.GRAY
        gc.lineWidth = 1.0

        // draw grid lines
        val offset = 50.0
        var i = offset
        while (i < w) {
            // vertical
            gc.strokeLine(i, 0.0, i, h)
            // horizontal
            gc.strokeLine(0.0, i, w, i)
            i += offset
        }

        children.add(grid)

        grid.toBack()
    }

    /**
     * Set x/y pivot points
     * @param x
     * @param y
     */
    fun setPivot(x: Double, y: Double) {
        translateX = translateX - x
        translateY = translateY - y
    }
}


/**
 * Mouse drag context used for scene and nodes.
 */
internal class DragContext {

    var mouseAnchorX: Double = 0.toDouble()
    var mouseAnchorY: Double = 0.toDouble()

    var translateAnchorX: Double = 0.toDouble()
    var translateAnchorY: Double = 0.toDouble()

}

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
internal class NodeGestures(var canvas: PannableCanvas) {

    private val nodeDragContext = DragContext()

    // left mouse button => dragging
    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown)
            return@EventHandler

        nodeDragContext.mouseAnchorX = event.sceneX
        nodeDragContext.mouseAnchorY = event.sceneY

        val node = event.source as Node

        nodeDragContext.translateAnchorX = node.translateX
        nodeDragContext.translateAnchorY = node.translateY
    }

    // left mouse button => dragging
    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isPrimaryButtonDown)
            return@EventHandler

        val scale = canvas.scale

        val node = event.source as Node

        node.translateX = nodeDragContext.translateAnchorX + (event.sceneX - nodeDragContext.mouseAnchorX) / scale
        node.translateY = nodeDragContext.translateAnchorY + (event.sceneY - nodeDragContext.mouseAnchorY) / scale

        event.consume()
    }
}

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
internal class SceneGestures(var canvas: PannableCanvas) {

    private val sceneDragContext = DragContext()

    // right mouse button => panning
    val onMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isSecondaryButtonDown)
            return@EventHandler

        sceneDragContext.mouseAnchorX = event.sceneX
        sceneDragContext.mouseAnchorY = event.sceneY

        sceneDragContext.translateAnchorX = canvas.translateX
        sceneDragContext.translateAnchorY = canvas.translateY
    }

    // right mouse button => panning
    val onMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { event ->
        if (!event.isSecondaryButtonDown)
            return@EventHandler

        canvas.translateX = sceneDragContext.translateAnchorX + event.sceneX - sceneDragContext.mouseAnchorX
        canvas.translateY = sceneDragContext.translateAnchorY + event.sceneY - sceneDragContext.mouseAnchorY

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

        println("${event.sceneX} ${canvas.boundsInParent.width} ${canvas.boundsInParent.minX}")
        println("${event.sceneY} ${canvas.boundsInParent.height} ${canvas.boundsInParent.minY}")

        val dx = event.sceneX - (canvas.boundsInParent.width / 2 + canvas.boundsInParent.minX)
        val dy = event.sceneY - (canvas.boundsInParent.height / 2 + canvas.boundsInParent.minY)

        canvas.scale = scale
        canvas.setPivot(f * dx, f * dy)

        event.consume()
    }

    companion object {

        private val MAX_SCALE = 10.0
        private val MIN_SCALE = .1


        fun clamp(value: Double, min: Double, max: Double): Double {

            if (java.lang.Double.compare(value, min) < 0)
                return min

            return if (java.lang.Double.compare(value, max) > 0) max else value

        }
    }
}


/**
 * An application with a zoomable and pannable canvas.
 */
class ZoomAndScrollApplication : Application() {

    override fun start(stage: Stage) {

        val group = Group()

        // create canvas
        val canvas = PannableCanvas()

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.translateX = 100.0
        canvas.translateY = 100.0

        // create sample nodes which can be dragged
        val nodeGestures = NodeGestures(canvas)

        val label1 = Label("Draggable node 1")
        label1.translateX = 10.0
        label1.translateY = 10.0
        label1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        label1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)

        val label2 = Label("Draggable node 2")
        label2.translateX = 100.0
        label2.translateY = 100.0
        label2.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        label2.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)

        val label3 = Label("Draggable node 3")
        label3.translateX = 200.0
        label3.translateY = 200.0
        label3.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        label3.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)

        val circle1 = Circle(300.0, 300.0, 50.0)
        circle1.stroke = Color.ORANGE
        circle1.fill = Color.ORANGE.deriveColor(1.0, 1.0, 1.0, 0.5)
        circle1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        circle1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)

        val rect1 = Rectangle(100.0, 100.0)
        rect1.translateX = 450.0
        rect1.translateY = 450.0
        rect1.stroke = Color.BLUE
        rect1.fill = Color.BLUE.deriveColor(1.0, 1.0, 1.0, 0.5)
        rect1.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        rect1.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)

        canvas.children.addAll(label1, label2, label3, circle1, rect1)

        group.children.add(canvas)

        val pane = Pane()
        pane.add(group)
        // create scene which can be dragged and zoomed
        val scene = Scene(pane, 1024.0, 768.0)

        val sceneGestures = SceneGestures(canvas)
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.onMousePressedEventHandler)
        pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.onMouseDraggedEventHandler)
        pane.addEventFilter(ScrollEvent.ANY, sceneGestures.onScrollEventHandler)

        stage.scene = scene
        stage.show()

        canvas.addGrid()

    }
}

fun main(args: Array<String>) {
    Application.launch(ZoomAndScrollApplication::class.java, *args)
}