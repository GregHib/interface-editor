package com.greg.view.canvas

import com.greg.controller.canvas.NodeGestures
import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.canvas.SceneGestures
import com.greg.controller.selection.MarqueeController
import com.greg.controller.selection.SelectionController
import com.greg.controller.widgets.WidgetShapeBuilder
import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.Widget
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.view.WidgetShape
import javafx.collections.ListChangeListener
import javafx.scene.Cursor
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.group
import tornadofx.pane
import tornadofx.removeFromParent

class CanvasView : View() {

    val widgets: WidgetsController by inject()
    private val canvas = PannableCanvas()
    private val nodeGestures = NodeGestures(widgets, canvas)
    private var marquee = MarqueeController(widgets, canvas)
    private val selection = SelectionController(widgets)
    private val sceneGestures = SceneGestures(canvas)

    private var spaceHeld = false
    private var horizontal = 0
    private var vertical = 0

    override val root = pane {
        val rectangle = Rectangle()
        rectangle.widthProperty().bind(widthProperty())
        rectangle.heightProperty().bind(heightProperty())
        clip = rectangle
        group {
            // create canvas

            // we don't want the canvas on the top/left in this example => just
            // translate it a bit
            canvas.layoutX = 100.0
            canvas.layoutY = 100.0

            // create sample nodes which can be dragged
//            createAndDisplay(WidgetType.TEXT)

//            createAndDisplay(WidgetType.TEXT)
            /*val grid = group {
                for(i in 0..canvas.prefWidth.toInt()/50) {
                    rectangle(i * 50, 0.0, 1.0, canvas.prefHeight) {
                        fill = Color.BLACK
                    }
                }
                for(i in 0..canvas.prefHeight.toInt()/50) {
                    rectangle(0.0, i * 50, canvas.prefWidth, 1.0) {
                        fill = Color.BLACK
                    }
                }
            }

            canvas.children.add(grid)*/

            add(canvas)

            this@pane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.onMousePressedEventHandler)
            this@pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.onMouseDraggedEventHandler)
            this@pane.addEventFilter(ScrollEvent.ANY, sceneGestures.onScrollEventHandler)

            primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, sceneGestures.onKeyPressedEventHandler)
            primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, sceneGestures.onKeyReleasedEventHandler)
        }

        primaryStage.addEventFilter(KeyEvent.ANY, { handleKeyEvents(it) })

        addEventFilter(MouseEvent.MOUSE_PRESSED, { event ->
            if (!spaceHeld)
                selection.start(event)

            val cloned = event.isShiftDown && widgets.hasSelection()
//            if(cloned)
//                movement.clone()

            //Start movement (and actions)
//            movement.start(event, pane)

            //If shift cloned start action with cloned widget
//            if(cloned)
//                widgets.start(widgets.getWidget(movement.getClone(event)))
//            else
//                widgets.start(widgets.getWidget(event.target))

            if (spaceHeld)
                cursor = Cursor.CLOSED_HAND
            else
                marquee.init(event)

        })

        addEventFilter(MouseEvent.MOUSE_DRAGGED, { event ->
            if (event.isPrimaryButtonDown) {
                //Transform marquee box to match mouse position
                if (!spaceHeld)
                    marquee.handle(event)
                else
                    cursor = Cursor.CLOSED_HAND
            }
        })

        addEventFilter(MouseEvent.MOUSE_RELEASED, { event ->
            marquee.select(event)

            if (spaceHeld)
                cursor = Cursor.OPEN_HAND

            widgets.finish()
        })

        addEventFilter(MouseEvent.MOUSE_CLICKED, { event ->
            if (event.clickCount == 2) {
                //            movement.resetClone()
                marquee.select(event)

                widgets.finish()
            } else
                requestFocus()
        })

    }

    private fun handleKeyEvents(it: KeyEvent) {
        when (it.eventType) {
            KeyEvent.KEY_PRESSED -> {
                if (it.code == KeyCode.SPACE) {
                    spaceHeld = true
                    if (root.cursor != Cursor.OPEN_HAND && root.cursor != Cursor.CLOSED_HAND) {
                        root.cursor = Cursor.OPEN_HAND
                        marquee.remove(root)
                    }
                } else
                    move(it)
            }
            KeyEvent.KEY_RELEASED -> {
                if (it.code == KeyCode.SPACE) {
                    spaceHeld = false
                    root.cursor = Cursor.DEFAULT
                } else
                    reset(it.code)
            }
        }

    }

    private fun move(event: KeyEvent) {
        when {
            event.code == KeyCode.RIGHT -> horizontal = 1
            event.code == KeyCode.LEFT -> horizontal = -1
            event.code == KeyCode.UP -> vertical = -1
            event.code == KeyCode.DOWN -> vertical = 1
        }

        move(if (event.isShiftDown) horizontal * 10 else horizontal, if (event.isShiftDown) vertical * 10 else vertical)
    }

    private fun move(x: Int, y: Int) {
        widgets.forSelected { widget ->
            widget.setX(widget.getX() + x)
            widget.setY(widget.getY() + y)
        }
    }

    private fun reset(code: KeyCode) {
        if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
            horizontal = 0

        if (code == KeyCode.UP || code == KeyCode.DOWN)
            vertical = 0
    }

    fun createAndDisplay(type: WidgetType) {
        val widget = WidgetBuilder(type).build()
        widgets.add(widget)
    }

    fun refresh(it: ListChangeListener.Change<out Widget>) {
        if (it.wasAdded()) {
            it.addedSubList.forEach { widget ->
                val shape = WidgetShapeBuilder(widget).build()
                shape.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
                shape.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)
                widgets.connect(widget, shape)
                canvas.children.add(shape)
            }
        } else if (it.wasRemoved()) {
            it.removed.forEach { widget ->
                canvas.children.filterIsInstance<WidgetShape>()
                        .filter { it.identifier == widget.identifier }
                        .forEach { it.removeFromParent() }
            }
        }
    }
}