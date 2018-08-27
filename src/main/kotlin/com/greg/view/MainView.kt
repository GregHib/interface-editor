package com.greg.view

import com.greg.controller.widgets.WidgetsController
import com.greg.model.cache.CacheController
import com.greg.model.settings.Settings
import com.greg.view.alerts.IntegerAlert
import com.greg.view.canvas.CanvasView
import javafx.scene.control.ButtonType
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
                item("Open cache folder").action {
                    cache.selectDirectory()
                }
                /*item("Open interface file").action {
                    cache.selectFile()
                }*/
                item("Save").action {
                    cache.save(widgets)
                }
                item("Load interface").action {
                    val alert = IntegerAlert("Interface ID:")
                    val result = alert.showAndWait()

                    if (result.get() == ButtonType.OK) {
                        widgets.clearSelection()
                        widgets.deleteAll()
                        canvas.defaultState()
                        cache.interfaces.display(widgets, alert.value)
                    }
                }
                item("Unlink cache").action {
                    widgets.clearSelection()
                    widgets.deleteAll()
                    canvas.defaultState()
                    cache.unlink()
                }
            }
            menu("Settings") {
                checkmenuitem("Night mode") {
                    isSelected = Settings.getBoolean(Settings.NIGHT_MODE)
                    if(isSelected)
                        this@borderpane.stylesheets.add("stylesheet.css")
                    action {
                        if (isSelected)
                            this@borderpane.stylesheets.add("stylesheet.css")
                        else
                            this@borderpane.stylesheets.remove("stylesheet.css")
                    }
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