package com.greg.view.canvas

import com.greg.controller.canvas.NodeGestures
import com.greg.controller.canvas.PannableCanvas
import com.greg.controller.canvas.SceneGestures
import com.greg.controller.widgets.WidgetShapeBuilder
import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.CacheController
import com.greg.model.widgets.WidgetBuilder
import com.greg.model.widgets.WidgetType
import com.greg.model.widgets.type.Widget
import com.greg.model.widgets.type.WidgetContainer
import com.greg.model.widgets.type.WidgetSprite
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
    private val cache: CacheController by inject()
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

    fun clear() {
        widgets.clearSelection()
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

        addEventFilter(MouseEvent.ANY) { handleMouseEvents(it)}

        /**
         * Dragging from component panel
         */

        setOnDragOver { event ->
            if (event.dragboard.hasString())
                event.acceptTransferModes(TransferMode.MOVE)

            event.consume()
        }

        setOnDragDropped { event ->
            val string = event.dragboard.string
            val data = if(string.contains(":")) string.split(":") else arrayListOf(string)
            val type = WidgetType.forString(data[0])
            if (type != null) {
                widgets.clearSelection()

                //Create
                val widget = WidgetBuilder(type).build()

                //Display
                widgets.add(widget)

                val scaleOffsetX = canvas.boundsInLocal.minX * canvas.scaleX
                val canvasX = canvas.boundsInParent.minX - scaleOffsetX

                val scaleOffsetY = canvas.boundsInLocal.minY * canvas.scaleY
                val canvasY = canvas.boundsInParent.minY - scaleOffsetY

                val dropX = (event.x - canvasX) / canvas.scaleX
                val dropY = (event.y - canvasY) / canvas.scaleY

                widget.setX(dropX.toInt())
                widget.setY(dropY.toInt())

                if(data.size >= 2) {
                    if(widget is WidgetSprite) {
                        widget.setDefaultSprite(Integer.valueOf(data[1]))
                        widget.setDefaultSpriteArchive(data[2])
                    }
                }

                widget.setSelected(true)
            }

            event.isDropCompleted = true

            this.requestFocus()

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
        if(!event.isControlDown && event.code == KeyCode.A) {
            val list = arrayListOf<Widget>()
            for(i in 0..400)
                list.add(WidgetBuilder(WidgetType.RECTANGLE).build())
            widgets.addAll(list.toTypedArray())
        }
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

    private fun createShape(widget: Widget, children: List<WidgetShape>? = null): WidgetShape {
        val shape = WidgetShapeBuilder(widget).build()
        shape.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.onMousePressedEventHandler)
        shape.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.onMouseDraggedEventHandler)
        shape.addEventFilter(MouseEvent.MOUSE_ENTERED, nodeGestures.onMouseEnteredEventHandler)
        shape.addEventFilter(MouseEvent.MOUSE_EXITED, nodeGestures.onMouseExitedEventHandler)
        widgets.connect(widget, shape, cache, children)
        return shape
    }

    private fun create(widgets: List<Widget>): List<WidgetShape> {
        val shapes = arrayListOf<WidgetShape>()

        widgets.forEach { widget ->
            if(widget is WidgetContainer) {
                //Create child shapes
                val children = create(widget.getChildren())
                //Create shape with children
                shapes.add(createShape(widget, children))
            } else {
                //Create shape
                shapes.add(createShape(widget))
            }
        }

        return shapes
    }
    fun refresh(it: ListChangeListener.Change<out Widget>) {
        if (it.wasAdded()) {
            val shapes = create(it.addedSubList)
            canvas.children.addAll(shapes)
        } else if (it.wasRemoved()) {
            it.removed.forEach { widget ->
                canvas.children.filterIsInstance<WidgetShape>()
                        .filter { it.identifier == widget.identifier }
                        .forEach { it.removeFromParent() }
            }
        }
    }

    val hierarchyListener: ListChangeListener<TreeItem<String>> = ListChangeListener { change ->
        change.next()
        if(change.wasAdded()) {
            //TODO probably a more efficient way of doing this
            change.list
                    .filterIsInstance<HierarchyItem>()
                    .forEach { item ->
                        canvas.children
                                .filterIsInstance<WidgetShape>()
                                .filter { it.identifier == item.identifier }
                                .forEach { it.toFront() }
                    }
        }
    }

}