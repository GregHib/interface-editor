package com.greg.controller.view

import javafx.animation.PauseTransition
import javafx.concurrent.Task
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.VBox
import javafx.util.Duration
import tornadofx.View

class ToolbarView : View() {
    override val root : VBox by fxml("/toolbar.fxml")

    private val progressIndicator: ProgressIndicator by fxid()

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