package com.greg.controller

import com.greg.App
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.WidgetRectangle
import com.greg.canvas.widget.WidgetText
import com.greg.properties.PropertyPanel
import com.greg.settings.Settings
import com.greg.settings.SettingsKey
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*

class Controller : Initializable {


    /**
     * Ideas
     *
     * On double click on a widget deselect all but that ( and black out rest of canvas? ) display stretching/rotation (photoshop crop like) - enter to finish/ esc to cancel
     *
     * Widget x/y which get's the widget location relative to the canvas: will be needed for saving. Might need canvas as a parameter
     *
     * Expand marquee out to it's own class with inheritance, so you can switch out marquee type/shape etc..
     *
     * Properties panel add different stuff for selection type, edit multiple selections if all the same class
     *
     * Move selection using arrow keys by 1px
     *
     * Round x/y to 1px (or is it already done cause mouse pos can't be .5 of a pixel?
     *
     */

    @FXML
    lateinit var propertyPanel: AnchorPane

    @FXML
    lateinit var widgetCanvas: Pane

    @FXML
    lateinit var progressIndicator: ProgressIndicator

    lateinit var canvas: WidgetCanvas

    lateinit var properties: PropertyPanel

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        canvas = WidgetCanvas(this)
        properties = PropertyPanel(this)
    }

    @FXML
    fun createWidget() {
    }

    @FXML
    fun createContainer() {
    }

    @FXML
    fun createRectangle() {
        var rectangle = WidgetRectangle(Settings.getDouble(SettingsKey.DEFAULT_POSITION_X), Settings.getDouble(SettingsKey.DEFAULT_POSITION_Y), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_WIDTH), Settings.getDouble(SettingsKey.DEFAULT_RECTANGLE_HEIGHT))
        widgetCanvas.children.add(rectangle)
    }

    @FXML
    fun createText() {
        var text = WidgetText(Settings.get(SettingsKey.DEFAULT_TEXT_MESSAGE), Settings.getColour(SettingsKey.DEFAULT_STROKE_COLOUR))
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