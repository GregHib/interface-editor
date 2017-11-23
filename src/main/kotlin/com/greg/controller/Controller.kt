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
import javafx.scene.control.ProgressBar
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration
import java.net.URL
import java.util.*

class Controller : Initializable {

    var mouseX = 0.0

    var mouseY = 0.0

    var click = 0

    private var offsetX = 0.0

    private var offsetY = 0.0

    @FXML
    lateinit var canvas: Canvas

    @FXML
    lateinit var pane: Pane

    @FXML
    lateinit var progressText: Text

    @FXML
    lateinit var progressBar: ProgressBar

    lateinit var selected: Group

    private var selectionModel = SelectionModel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        RubberBandSelection(pane, selectionModel)

        var text = WidgetText("Here is some text")
        text.textOrigin = VPos.TOP
        pane.children.add(text)
        text.stroke = Color.WHITE
        var rectangle = WidgetRectangle(50.5, 50.5, 10.0, 10.0)
        rectangle.stroke = Color.WHITE
        pane.children.add(rectangle)
        var rectangle2 = WidgetRectangle(45.5, 45.5, 10.0, 10.0)
        rectangle2.stroke = Color.WHITE
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
                updateProgress(1, 1)

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

        println(progressBar == null)
        progressBar?.isVisible = true

        progressBar?.progressProperty()!!.unbind()
        progressBar?.progressProperty().bind(task.progressProperty())

        progressText?.textProperty()!!.unbind()
        progressText.textProperty().bind(task.messageProperty())

        Thread(task).start()

        task.setOnSucceeded {

            val pause = PauseTransition(Duration.seconds(1.0))

            pause.setOnFinished {
                progressBar.isVisible = false
                progressText.textProperty().unbind()
                progressText.text = ""
            }

            pause.play()
        }

        task.setOnFailed {

            val pause = PauseTransition(Duration.seconds(1.0))

            pause.setOnFinished {
                progressBar.isVisible = false
                progressText.textProperty().unbind()
                progressText.text = ""
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