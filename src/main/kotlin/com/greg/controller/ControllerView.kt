package com.greg.controller

import com.greg.settings.Settings
import com.greg.ui.canvas.Canvas
import com.greg.ui.canvas.widget.Widgets
import com.greg.ui.canvas.widget.builder.WidgetBuilder
import com.greg.ui.canvas.widget.builder.data.WidgetFacade
import com.greg.ui.canvas.widget.type.WidgetType
import com.greg.ui.canvas.widget.type.types.WidgetGroup
import com.greg.ui.canvas.widget.type.types.WidgetRectangle
import com.greg.ui.canvas.widget.type.types.WidgetText
import com.greg.ui.hierarchy.HierarchyManager
import com.greg.ui.panel.PanelManager
import javafx.animation.PauseTransition
import javafx.concurrent.Task
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.util.Duration
import tornadofx.View
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties


class ControllerView : View() {
    override val root : BorderPane by fxml("/main.fxml")

    val widgetCanvas: Pane by fxid()
    private val progressIndicator: ProgressIndicator by fxid()
    val hierarchyTree: TreeView<String> by fxid()
    private val right: StackPane by fxid()

    var canvas: Canvas

    var panels: PanelManager

    var widgets: Widgets

    var hierarchy: HierarchyManager

    init {
        title = "Greg's Interface Editor"
        primaryStage.isResizable = false
        primaryStage.sizeToScene()

        Settings.clear()

        val start = System.currentTimeMillis()
        preload(WidgetText::class)
        preload(WidgetRectangle::class)
        preload(WidgetGroup::class)
        println("Preload complete in ${System.currentTimeMillis() - start}ms")

        widgets = Widgets(this)
        canvas = Canvas(this)
        panels = PanelManager(this)
        hierarchy = HierarchyManager(this)


        right.add(panels)
    }

    private fun preload(kClass: KClass<out WidgetFacade>) {
        kClass.memberFunctions
        kClass.memberProperties
    }

    fun handleKeyPress(event: KeyEvent) {
        hierarchyTree.refresh()
        canvas.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        canvas.handleKeyRelease(event)
    }

    fun createWidget() {

    }

    fun createContainer() {

    }

    fun createRectangle() {
        val widget = WidgetBuilder().build()
        widgets.add(widget)
        hierarchy.add(widget)
    }

    fun createText() {
        val widget = WidgetBuilder(WidgetType.TEXT).build()
        widgets.add(widget)
        hierarchy.add(widget)
    }

    fun createSprite() {

    }

    fun createModel() {

    }

    fun createTooltip() {

    }

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
}