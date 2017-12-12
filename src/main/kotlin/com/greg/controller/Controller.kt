package com.greg.controller

import com.greg.App
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.WidgetBuilder
import com.greg.panels.attributes.parts.AttributesPanel
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Accordion
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*

class Controller : Initializable {


    /**
     * Ideas
     *
     * https://gyazo.com/407615643f5a46f34ee82f60252ec86e
     *
     * classType can probably be removed with all the this::class's for creating attributes as you can get the type directly from the widget
     *
     * On double click on a widget deselect all but that ( and black out rest of canvas? ) display stretching/rotation (photo shop crop like) - enter to finish/ esc to cancel
     *
     * Widget x/y which get's the widget location relative to the canvas: will be needed for saving. Might need canvas as a parameter
     *
     * Expand marquee out to it's own class with inheritance, so you can switch out marquee type/shape etc..
     *
     * Move selection using arrow keys by 1px
     *
     * Undo/redo links?
     *
     * New properties to add: width height, x, y (When changing text don't resize x/y?)
     *
     * Two different Nodes on a property row, different layout and how they'd all link
     * https://gyazo.com/fb3f3a596c270d886fe116ac7188cd56
     * https://gyazo.com/d17ac37a4aee18625f0037c36aba52d5
     *
     */

    @FXML
    lateinit var attributesPanel: Accordion

    @FXML
    lateinit var widgetCanvas: Pane

    @FXML
    lateinit var progressIndicator: ProgressIndicator

    lateinit var canvas: WidgetCanvas

    lateinit var attributes: AttributesPanel

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        canvas = WidgetCanvas(this)
        attributes = AttributesPanel(this)
    }

    @FXML
    fun createWidget() {
    }

    @FXML
    fun createContainer() {
    }

    @FXML
    fun createRectangle() {
        widgetCanvas.children.add(WidgetBuilder().build())
    }

    @FXML
    fun createText() {
        val builder = WidgetBuilder()
        builder.addText()
        widgetCanvas.children.add(builder.build())
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