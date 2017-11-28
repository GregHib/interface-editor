package com.greg.controller

import com.greg.App
import com.greg.selection.RubberBandSelection
import com.greg.selection.SelectionModel
import com.greg.widget.WidgetRectangle
import com.greg.widget.WidgetText
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.VPos
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.MouseEvent
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

    @FXML private
    lateinit var canvas: Canvas

    @FXML
    lateinit var pane: Pane

    @FXML
    lateinit var progressIndicator: ProgressIndicator

    lateinit var selected: Group

    private var selectionModel = SelectionModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        RubberBandSelection(pane, selectionModel)

        var text = WidgetText("Here is some text", Color.WHITE)
        text.textOrigin = VPos.TOP
        pane.children.add(text)
        text.setStroke(Color.WHITE)
        var rectangle = WidgetRectangle(50.5, 50.5, 10.0, 10.0)
        rectangle.setStroke(Color.WHITE)
        pane.children.add(rectangle)
        var rectangle2 = WidgetRectangle(45.5, 45.5, 10.0, 10.0)
        rectangle2.setStroke(Color.WHITE)
        pane.children.add(rectangle2)

        selected = Group()
        pane.children.add(selected)
        selected.toFront()
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

    @FXML
    fun handleMouseDragged(event: MouseEvent) {
        val stage = App.mainStage

        stage.x = event.screenX - offsetX
        stage.y = event.screenY - offsetY
    }

    @FXML
    fun handleMousePressed(event: MouseEvent) {
        val boundsInScene = canvas.localToScene(canvas.boundsInLocal)
        offsetX = event.sceneX
        offsetY = event.sceneY
        mouseX = event.sceneX - boundsInScene.minX
        mouseY = event.sceneY - boundsInScene.minY
        click = event.button.ordinal
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