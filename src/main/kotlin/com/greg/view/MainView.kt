package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.CacheController
import com.greg.view.canvas.CanvasView
import javafx.scene.input.KeyEvent
import javafx.scene.shape.Rectangle
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane


class MainView : View("Greg's Interface Editor") {

    private val widgets: WidgetsController by inject()
    private val cache: CacheController by inject()

    private val canvas = CanvasView()
    private val rightPane = RightPane()
    private val leftPane = LeftPane()

    init {
        primaryStage.addEventFilter(KeyEvent.ANY) {
            canvas.handleKeyEvents(it)
            leftPane.handleKeyEvents(it)
        }

        widgets.start(canvas)

        leftPane.hierarchy.rootTreeItem.children.addListener(canvas.hierarchyListener)
    }

    override val root = borderpane {
        primaryStage.minWidth = 600.0
        primaryStage.minHeight = 420.0
        primaryStage.width = 1280.0
        primaryStage.height = 768.0

        prefWidth = 1280.0
        prefHeight = 768.0
        top = menubar {
            menu("File") {
                item("Load cache").action {
                    cache.select()
                }
            }
        }
        left = leftPane.root
        right = rightPane.root
        center = pane {
            val rectangle = Rectangle()
            rectangle.widthProperty().bind(widthProperty())
            rectangle.heightProperty().bind(heightProperty())
            clip = rectangle
            notificationPane {
                /*val imagePath = MainView::class.java.getResource("/notification-pane-warning.png").toExternalForm()
                val image = ImageView(imagePath)
                graphic = image*/
                content {
                    stackpane {

                        add(canvas)
                        /*setOnMouseClicked {
                            if (this@notificationPane.isShowing) {
                                this@notificationPane.hide()
                            } else {
                                this@notificationPane.show()
                            }
                        }*/
                        prefWidthProperty().bind(this@pane.widthProperty())
                        prefHeightProperty().bind(this@pane.heightProperty())
                    }
                }
            }
        }
    }
}