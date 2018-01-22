package com.greg.view

import javafx.scene.shape.Rectangle
import com.greg.view.LeftPane
import com.greg.view.RightPane
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane


class MainView : View() {

    val canvas = CanvasView()
    val rightPane = RightPane()

    override val root = borderpane {
        primaryStage.minWidth = 600.0
        primaryStage.minHeight = 420.0
        primaryStage.width = 1280.0
        primaryStage.height = 768.0

        setOnMouseClicked {
            rightPane.onSelection.invoke("Clicked")
        }
        prefWidth = 1280.0
        prefHeight = 768.0
        top = menubar {
            menu("File")
        }
        left = LeftPane().root
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