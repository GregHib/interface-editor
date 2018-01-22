package src.com.greg.view

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.shape.Rectangle
import src.com.greg.controller.MarqueeController
import src.com.greg.controller.SelectionController
import src.com.greg.controller.canvas.NodeGestures
import src.com.greg.controller.canvas.PannableCanvas
import src.com.greg.controller.canvas.SceneGestures
import src.com.greg.controller.widgets.WidgetShapeBuilder
import src.com.greg.controller.widgets.WidgetsController
import src.com.greg.model.widgets.WidgetBuilder
import src.com.greg.model.widgets.WidgetType
import tornadofx.*

class CanvasView : View() {

    val widgets: WidgetsController by inject()
    private val canvas = PannableCanvas()
    private val nodeGestures = NodeGestures(widgets, canvas)
    private var marquee = MarqueeController(widgets, canvas)
    private val selection = SelectionController(widgets)
    private val sceneGestures = SceneGestures(canvas)

    init {
        widgets.getAll().onChange {
            it.next()
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
            createAndDisplay(WidgetType.TEXT)

            createAndDisplay(WidgetType.TEXT)
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
            if(!spaceHeld)
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

            if(!spaceHeld)
                marquee.init(event)
        })

        addEventFilter(MouseEvent.MOUSE_DRAGGED, { event ->
            if (event.isPrimaryButtonDown) {
                //Transform marquee box to match mouse position
                if(!spaceHeld)
                    marquee.handle(event)
            }
        })

        addEventFilter(MouseEvent.MOUSE_RELEASED, { event ->
            marquee.select(event)

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
    var spaceHeld = false

    private fun handleKeyEvents(it: KeyEvent) {
        when (it.eventType) {
            KeyEvent.KEY_PRESSED -> {
                if (it.code == KeyCode.SPACE)
                    spaceHeld = true
            }
            KeyEvent.KEY_RELEASED -> {
                if (it.code == KeyCode.SPACE)
                    spaceHeld = false
            }
        }

    }

    private fun createAndDisplay(type: WidgetType) {
        val widget = WidgetBuilder(type).build()
        widgets.add(widget)
    }
}