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
import javafx.scene.input.*
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

    private var cloned = false
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

            val clone = event.isShiftDown && widgets.hasSelection()
            if (clone) {
                if (!cloned) {
                    widgets.clone()
                    cloned = true
                }
            }

            //Start movement (and actions)
//            movement.start(event, pane)

            //If shift cloned start action with cloned widget
            if (clone)
                widgets.start(widgets.getWidget(getClone(event)))
            else
                widgets.start(widgets.getWidget(event.target))

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
            cloned = false
            marquee.select(event)

            if (spaceHeld)
                cursor = Cursor.OPEN_HAND

            widgets.finish()
        })

        addEventFilter(MouseEvent.MOUSE_CLICKED, { event ->
            if (event.clickCount == 2) {
                cloned = false
                marquee.select(event)

                widgets.finish()
            } else
                requestFocus()
        })

        setOnDragOver { event ->
            if (event.dragboard.hasString())
                event.acceptTransferModes(TransferMode.MOVE)


            event.consume()
        }

        setOnDragDropped { event ->
            val type = WidgetType.forString(event.dragboard.string)
            if (type != null) {
                val widget = createAndDisplay(type)

                val scaleOffsetX = canvas.boundsInLocal.minX * canvas.scaleX
                val canvasX = canvas.boundsInParent.minX - scaleOffsetX

                val scaleOffsetY = canvas.boundsInLocal.minY * canvas.scaleY
                val canvasY = canvas.boundsInParent.minY - scaleOffsetY

                val dropX = (event.x - canvasX) / canvas.scaleX
                val dropY = (event.y - canvasY) / canvas.scaleY

                widget.setX(dropX.toInt())
                widget.setY(dropY.toInt())

                widget.setSelected(true)
            }

            event.isDropCompleted = true

            event.consume()
        }
    }

    private fun getClone(event: MouseEvent): WidgetShape? {
        return canvas.children.filterIsInstance<WidgetShape>()
                .firstOrNull { it.boundsInParent.intersects(event.x, event.y, 1.0, 1.0) }
    }

    private fun handleKeyEvents(event: KeyEvent) {
        if (root.isFocused) {
            when (event.eventType) {
                KeyEvent.KEY_PRESSED -> handleKeyPress(event)
                KeyEvent.KEY_RELEASED -> handleKeyRelease(event)
            }

            event.consume()
        }
    }

    private fun handleKeyPress(event: KeyEvent) {
        if (event.code == KeyCode.SPACE) {
            spaceHeld = true
            if (root.cursor != Cursor.OPEN_HAND && root.cursor != Cursor.CLOSED_HAND) {
                root.cursor = Cursor.OPEN_HAND
                marquee.remove(root)
            }
            return
        }

        if (event.code != KeyCode.SHIFT)
            widgets.start()

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            move(event)
    }

    private fun handleKeyRelease(event: KeyEvent) {
        if (event.code == KeyCode.SPACE) {
            spaceHeld = false
            root.cursor = Cursor.DEFAULT
            return
        }

        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.code == KeyCode.UP || event.code == KeyCode.DOWN)
            reset(event.code)
        else if (event.code == KeyCode.DELETE)
            widgets.deleteSelection()
        else if (!event.isShiftDown)
            cloned = false

        if (event.isControlDown) {
            when (event.code) {
                KeyCode.A -> {
                    widgets.selectAll()
                }
                KeyCode.X -> {
                    widgets.cut()
                }
                KeyCode.C -> {
                    widgets.copy()
                }
                KeyCode.V -> {
                    widgets.paste()
                }
                KeyCode.Z -> {
                    if (event.isShiftDown)
                        widgets.redo()
                    else
                        widgets.undo()
                }
                else -> {
                }
            }
        }

        if (event.code != KeyCode.SHIFT)
            widgets.finish()
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

    fun createAndDisplay(type: WidgetType): Widget {
        val widget = WidgetBuilder(type).build()
        widgets.add(widget)
        return widget
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