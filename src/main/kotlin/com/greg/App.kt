package com.greg

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle


class App : Application() {

    override fun start(stage: Stage) {
        mainStage = stage
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/main.fxml"))
        stage.title = "Better interface editor"
        stage.scene = Scene(root)
        stage.initStyle(StageStyle.UNDECORATED)
        stage.show()

//        drawLayered()
    }

    private fun drawLayered() {
        val sharpCanvas = createCanvasGrid(600.0, 300.0, 16, Color.GRAY)
        val sharpCanvas2 = createCanvasGrid(600.0, 300.0, 32, Color.BLACK)
        val group = BorderPane()
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

    companion object {

        lateinit var mainStage : Stage

        @JvmStatic
        fun main(args : Array<String>) {
            launch(App::class.java)
        }
    }
}