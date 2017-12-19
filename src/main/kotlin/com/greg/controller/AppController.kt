package com.greg.controller

import com.greg.App
import com.greg.canvas.WidgetCanvas
import com.greg.canvas.widget.*
import com.greg.panels.attributes.parts.AttributesPanel
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Accordion
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class AppController : Initializable {


    /**
     * https://gyazo.com/407615643f5a46f34ee82f60252ec86e
     *
     *
     * Ideas
     *
     * Deleting objects
     *
     * Copying/cloning existing objects
     *
     * Arrow key movement extract to class and implement in edit controller
     *
     * Send back one, bring forward one, send to back, bring to front (but not in front of edit mode components)
     *
     * classType can probably be removed with all the this::class's for creating attributes as you can get the type directly from the widget
     *
     * On double click on a widget deselect all but that ( and black out rest of canvas? ) display stretching/rotation (photo shop crop like) - enter to finish/ esc to cancel
     *
     * Widget x/y which get's the widget location relative to the canvas: will be needed for saving. Might need canvas as a parameter
     *
     * Expand marquee out to it's own class with inheritance, so you can switch out marquee type/shape etc..
     *
     * The way the edit mode is refresh can be improved.
     *
     * Also widget change listener has room for optimisations as it refresh's multiple times if x/y width/height are changed simultaneously
     *
     * Move selection using arrow keys by 1px
     *
     * Advanced arrow key movement system, ideally without a game-loop see : https://stackoverflow.com/questions/21331519/how-to-get-smooth-animation-with-keypress-event-in-javafx
     *
     * Resize with shift = aspect ratio
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
        val start = System.currentTimeMillis()
        preload(WidgetText::class)
        preload(WidgetRectangle::class)
        preload(Widget::class)
        println("Preload complete in ${System.currentTimeMillis() - start}ms")

        canvas = WidgetCanvas(this)
        attributes = AttributesPanel(this)
    }

    private fun preload(kClass: KClass<out AttributeWidget>) {
        kClass.memberFunctions
        kClass.memberProperties
    }

    @FXML
    fun handleKeyPress(event: KeyEvent) {
        canvas.handleKeyPress(event)
    }

    @FXML
    fun handleKeyRelease(event: KeyEvent) {
        canvas.handleKeyRelease(event)
    }

    @FXML
    fun createWidget() {
    }

    @FXML
    fun createContainer() {
    }

    @FXML
    fun createRectangle() {
        widgetCanvas.children.add(WidgetBuilder(canvas).build())
    }

    @FXML
    fun createText() {
        val builder = WidgetBuilder(canvas)
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