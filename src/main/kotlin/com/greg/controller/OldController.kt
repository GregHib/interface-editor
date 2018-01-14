package com.greg.controller

import com.greg.controller.view.CanvasView
import com.greg.controller.view.HierarchyView
import com.greg.controller.view.PanelView
import com.greg.controller.view.ToolbarView
import com.greg.settings.Settings
import com.greg.ui.action.ActionManager
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
import javafx.scene.input.KeyEvent
import tornadofx.View
import tornadofx.borderpane
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties


class OldController : View() {
    val canvasView: CanvasView by inject()
    val toolbarView: ToolbarView by inject()
    val panelView: PanelView by inject()
    val hierarchyView: HierarchyView by inject()

    override val root = borderpane {
        top = toolbarView.root
        center = canvasView.root
        right = panelView.root
        left = hierarchyView.root
    }

    var canvas: Canvas

    var panels: PanelManager

    var widgets: Widgets

    var manager: ActionManager

    var hierarchy: HierarchyManager

    init {
        title = "Greg's Interface Editor"
        primaryStage.isResizable = false
        primaryStage.sizeToScene()

        Settings.clear()

        preload()

        widgets = Widgets(this)
        canvas = Canvas(this)
        manager = ActionManager(widgets, canvas)
        panels = PanelManager(this)
        hierarchy = HierarchyManager(this)


        with(root) {
            right.add(panels)
        }

        root.setOnKeyPressed { event -> handleKeyPress(event) }
        root.setOnKeyReleased { event -> handleKeyRelease(event) }
    }

    private fun preload() {
        val start = System.currentTimeMillis()
        preload(WidgetText::class)
        preload(WidgetRectangle::class)
        preload(WidgetGroup::class)
        println("Preload complete in ${System.currentTimeMillis() - start}ms")
    }

    private fun preload(kClass: KClass<out WidgetFacade>) {
        kClass.memberFunctions
        kClass.memberProperties
    }

    fun handleKeyPress(event: KeyEvent) {
        canvasView.handleKeyPress(event)
        panelView.handleKeyPress(event)
        hierarchyView.handleKeyPress(event)
    }

    fun handleKeyRelease(event: KeyEvent) {
        canvasView.handleKeyRelease(event)
        panelView.handleKeyRelease(event)
        hierarchyView.handleKeyRelease(event)
    }

    fun createWidget() {

    }

    fun createContainer() {

    }

    fun createRectangle() {
    }

    fun createText() {
        val widget = WidgetBuilder(WidgetType.TEXT).build()
        widgets.add(widget)
    }

    fun createSprite() {

    }

    fun createModel() {

    }

    fun createTooltip() {

    }
}