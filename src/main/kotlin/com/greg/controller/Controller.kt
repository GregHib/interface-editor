package com.greg.controller

import com.greg.App
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.builder.WidgetBuilder
import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import com.greg.ui.canvas.widget.type.types.WidgetText
import com.greg.ui.panel.PanelManager
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

class Controller : Initializable {

    @FXML
    lateinit var attributesPanel: Accordion

    @FXML
    lateinit var widgetCanvas: Pane

    @FXML
    lateinit var progressIndicator: ProgressIndicator

    lateinit var canvas: Canvas

    lateinit var attributes: PanelManager

    lateinit var widgets: Widgets

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val start = System.currentTimeMillis()
        preload(WidgetText::class)
        preload(WidgetRectangle::class)
        preload(WidgetGroup::class)
        println("Preload complete in ${System.currentTimeMillis() - start}ms")

        widgets = Widgets(this)
        canvas = Canvas(this)
        attributes = PanelManager(this)
    }

    private fun preload(kClass: KClass<out WidgetFacade>) {
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
        widgets.add(WidgetBuilder().build())
    }

    @FXML
    fun createText() {
        widgets.add(WidgetBuilder(WidgetType.TEXT).build())
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