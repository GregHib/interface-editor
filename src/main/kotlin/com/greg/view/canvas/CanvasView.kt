package com.greg.view.canvas

import com.greg.controller.canvas.NodeGestures
import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.canvas.SceneGestures
import com.greg.controller.widgets.WidgetShapeBuilder
import com.greg.controller.widgets.WidgetsController
import com.greg.model.widgets.Widget
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.view.KeyInterface
import com.greg.view.canvas.states.DefaultState
import com.greg.view.canvas.states.EditState
import com.greg.view.canvas.widgets.WidgetShape
import com.greg.view.hierarchy.HierarchyItem
import javafx.collections.ListChangeListener
import javafx.scene.Cursor
import javafx.scene.control.TreeItem
import javafx.scene.input.*
import javafx.scene.shape.Rectangle
import tornadofx.View
import tornadofx.group
import tornadofx.pane
import tornadofx.removeFromParent

class CanvasView : View(), KeyInterface {

    companion object {
        var spaceHeld = false
    }

    private val widgets: WidgetsController by inject()
    private val canvas = PannableCanvas()
    private val nodeGestures = NodeGestures(widgets)
    private val sceneGestures = SceneGestures(canvas)

    private var state: CanvasState = DefaultState(this, canvas, widgets)

    fun defaultState() {
        closeState()
        state = DefaultState(this, canvas, widgets)
    }

    fun editState(widget: Widget, shape: WidgetShape) {
        closeState()
        state = EditState(this, widget, shape, widgets, canvas)
    }

    private fun closeState() {
        state.onClose()
    }

    override val root = pane {
        //Clip canvas to view
        val rectangle = Rectangle()
        rectangle.widthProperty().bind(widthProperty())
        rectangle.heightProperty().bind(heightProperty())
        clip = rectangle

        group {
            //Create canvas
            canvas.layoutX = 100.0
            canvas.layoutY = 100.0

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

        addEventFilter(MouseEvent.ANY, { handleMouseEvents(it)})

        /**
         * Dragging from component panel
         */

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

                widgets.clearSelection()

                widget.setSelected(true)
            }

            event.isDropCompleted = true

            event.consume()
        }
    }

    private fun handleMouseEvents(event: MouseEvent) {
        when(event.eventType) {
            MouseEvent.MOUSE_PRESSED -> state.handleMousePress(event)
            MouseEvent.MOUSE_DRAGGED -> state.handleMouseDrag(event)
            MouseEvent.MOUSE_RELEASED -> state.handleMouseRelease(event)
            MouseEvent.MOUSE_CLICKED -> {
                if(event.clickCount == 2)
                    state.handleDoubleClick(event)
                else
                    state.handleMouseClick(event)
            }
        }
    }

    /**
     * Keyboard event handlers
     */

    override fun handleKeyEvents(event: KeyEvent) {
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
            if (root.cursor != Cursor.OPEN_HAND && root.cursor != Cursor.CLOSED_HAND)
                root.cursor = Cursor.OPEN_HAND
        }

        state.handleKeyPress(event)
    }

    private fun handleKeyRelease(event: KeyEvent) {
        if (event.code == KeyCode.SPACE) {
            spaceHeld = false
            root.cursor = Cursor.DEFAULT
            return
        }

        state.handleKeyRelease(event)
    }

    /**
     * Misc
     */

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

    val hierarchyListener:ListChangeListener<TreeItem<String>> = ListChangeListener {
        it.next()
        if(it.wasAdded()) {
            //TODO probably a more efficient way of doing this
            it.list
                    .filterIsInstance<HierarchyItem>()
                    .forEach { item ->
                        canvas.children.filterIsInstance<WidgetShape>()
                                .filter { it.identifier == item.identifier }
                                .forEach { it.toFront() }
                    }
        }
    }

}