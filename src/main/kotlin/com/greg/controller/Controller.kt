package com.greg.controller

import com.greg.App
import com.greg.canvas.WidgetCanvas
import com.greg.widget.WidgetRectangle
import com.greg.widget.WidgetText
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration
import java.net.URL
import java.util.*

class Controller : Initializable {

    private var mouseX = 0.0

    private var mouseY = 0.0

    private var click = 0

    private var offsetX = 0.0

    private var offsetY = 0.0

    @FXML
    lateinit var widgetCanvas: Pane

    @FXML
    lateinit var progressIndicator: ProgressIndicator

    lateinit var selected: Group

    private lateinit var canvas: WidgetCanvas

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        canvas = WidgetCanvas(widgetCanvas)

        /*RubberBandSelection(widgetCanvas, selectionModel)

        var text = WidgetText("Here is some text", Color.WHITE)
        text.textOrigin = VPos.TOP
        widgetCanvas.children.add(text)
        text.setStroke(Color.WHITE)
        var rectangle = WidgetRectangle(50.5, 50.5, 10.0, 10.0)
        rectangle.setStroke(Color.WHITE)
        widgetCanvas.children.add(rectangle)
        var rectangle2 = WidgetRectangle(45.5, 45.5, 10.0, 10.0)
        rectangle2.setStroke(Color.WHITE)
        widgetCanvas.children.add(rectangle2)

        selected = Group()
        widgetCanvas.children.add(selected)
        selected.toFront()*/
    }

    @FXML
    fun createWidget() {
    }

    @FXML
    fun createContainer() {
    }

    @FXML
    fun createRectangle() {
        var bounds = widgetCanvas.layoutBounds
        var rectangle = WidgetRectangle(bounds.width / 2.0, bounds.height / 2.0, 10.0, 10.0)
        rectangle.setStroke(Color.WHITE)
        widgetCanvas.children.add(rectangle)
    }

    @FXML
    fun createText() {
        var text = WidgetText("Text", Color.WHITE)
        text.textOrigin = VPos.TOP
        widgetCanvas.children.add(text)
    }

    @FXML
    fun createSprite() {
    }

    @FXML
    fun createModel() {
    }

    @FXML
    fun createTooltip() {
    }

    @FXML
    fun loadCache() {

        createTask(object : Task<Boolean>() {

            @Throws(Exception::class)
            override fun call(): Boolean? {
                val progress = 100.00

                updateMessage(String.format("%.2f%s", progress, "%"))
                updateProgress(100, 100)
                return true
            }

        })
    }

    private fun createTask(task: Task<*>) {

        progressIndicator.isVisible = true

//        progressIndicator.progressProperty()!!.unbind()
//        progressIndicator.progressProperty().bind(task.progressProperty())

        Thread(task).start()

        task.setOnSucceeded {

            val pause = PauseTransition(Duration.seconds(1.0))

            pause.setOnFinished {
                progressIndicator.isVisible = false
            }

            pause.play()
        }

        task.setOnFailed {

            val pause = PauseTransition(Duration.seconds(1.0))

            pause.setOnFinished {
                progressIndicator.isVisible = false
            }

            pause.play()

        }
    }

    @FXML
    fun minimizeProgram() {
        App.mainStage.isIconified = true
    }

    @FXML
    fun closeProgram() {
        Platform.exit()
    }
}