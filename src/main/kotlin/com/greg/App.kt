package com.greg

import com.greg.selection.RubberBandSelection
import com.greg.selection.SelectionModel
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.scene.shape.Circle
import javafx.scene.Cursor.CROSSHAIR
import com.sun.javafx.robot.impl.FXRobotHelper.getChildren
import javafx.application.Application.launch
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import java.util.Collections.addAll
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import com.sun.javafx.robot.impl.FXRobotHelper.getChildren
import javafx.scene.layout.StackPane
import javafx.scene.text.Text


class App : Application() {


    var circle_Red: Circle? = null
    var circle_Green:Circle? = null
    var circle_Blue:Circle? = null

    var orgSceneX: Double = 0.toDouble()
    var orgSceneY:Double = 0.toDouble()
    var orgTranslateX: Double = 0.toDouble()
    var orgTranslateY:Double = 0.toDouble()

    override fun start(stage: Stage) {
        mainStage = stage
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/main.fxml"))
        stage.title = "Better interface editor"

//        group.children.addAll(sharpCanvas, sharpCanva2)


        stage.scene = Scene(root)
        stage.initStyle(StageStyle.UNDECORATED)
        stage.show()
    }

    private fun drawLayered() {
        val sharpCanvas = createCanvasGrid(600.0, 300.0, 16, Color.GRAY)
        val sharpCanvas2 = createCanvasGrid(600.0, 300.0, 32, Color.BLACK)
        var group = BorderPane()
        val pane = Pane()
        pane.children.add(sharpCanvas)
        pane.children.add(sharpCanvas2)
        sharpCanvas2.toFront()
        group.center = pane
        val root = VBox()
        root.children.add(group)
        mainStage.scene = Scene(root)
    }

    private fun createCanvasGrid(width: Double, height: Double, spacing: Int, stroke: Color): Canvas {
        val canvas = Canvas(width, height)
        val gc = canvas.graphicsContext2D
        gc.lineWidth = 1.0
        gc.stroke = stroke
        var x = 0
        while (x < width) {
            val x1: Double = x + 0.5
            gc.moveTo(x1, 0.0)
            gc.lineTo(x1, height)
            gc.stroke()
            x += spacing
        }

        var y = 0
        while (y < height) {
            val y1: Double = y + 0.5
            gc.moveTo(0.0, y1)
            gc.lineTo(width, y1)
            gc.stroke()
            y += spacing
        }
        return canvas
    }

    fun dragExample() {
        circle_Red = Circle(50.0, Color.RED)
        circle_Red!!.cursor = Cursor.HAND
        circle_Red!!.onMousePressed = circleOnMousePressedEventHandler
        circle_Red!!.onMouseDragged = circleOnMouseDraggedEventHandler

        circle_Green = Circle(50.0, Color.GREEN)
        circle_Green!!.cursor = Cursor.MOVE
        circle_Green!!.centerX = 150.0
        circle_Green!!.centerY = 150.0
        circle_Green!!.onMousePressed = circleOnMousePressedEventHandler
        circle_Green!!.onMouseDragged = circleOnMouseDraggedEventHandler

        circle_Blue = Circle(50.0, Color.BLUE)
        circle_Blue!!.cursor = Cursor.CROSSHAIR
        circle_Blue!!.translateX = 300.0
        circle_Blue!!.translateY = 100.0
        circle_Blue!!.onMousePressed = circleOnMousePressedEventHandler
        circle_Blue!!.onMouseDragged = circleOnMouseDraggedEventHandler
        val root = Group()
        root.children.addAll(circle_Red, circle_Green, circle_Blue)

        mainStage.isResizable = false
        mainStage.scene = Scene(root, 400.0, 350.0)
    }

    private var circleOnMousePressedEventHandler: EventHandler<MouseEvent> = EventHandler { t ->
        orgSceneX = t.sceneX
        orgSceneY = t.sceneY
        orgTranslateX = (t.source as Circle).translateX
        orgTranslateY = (t.source as Circle).translateY
    }

    var circleOnMouseDraggedEventHandler: EventHandler<MouseEvent> = EventHandler { t ->
        val offsetX = t.sceneX - orgSceneX
        val offsetY = t.sceneY - orgSceneY
        val newTranslateX = orgTranslateX + offsetX
        val newTranslateY = orgTranslateY + offsetY

        (t.source as Circle).translateX = newTranslateX
        (t.source as Circle).translateY = newTranslateY
    }

    companion object {

        lateinit var mainStage : Stage

        @JvmStatic
        fun main(args : Array<String>) {
            launch(App::class.java)
        }
    }
}