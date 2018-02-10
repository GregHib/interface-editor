package com.greg.view

import com.greg.model.widgets.WidgetType
import javafx.geometry.Insets
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeType
import tornadofx.*

class ComponentView : View() {
    override val root = tilepane {
        hgap = 10.0
        vgap = 10.0
        val types = WidgetType.values().toList()

        types
                .filterNot { it.hidden }
                .forEach {
                    vbox {
                        padding = Insets(10.0, 0.0, 0.0, 0.0)
                        val image = resources.image("${it.name.toLowerCase()}.png")
                        stackpane {
                            rectangle {
                                width = 46.0
                                height = 46.0
                                fill = Color.TRANSPARENT
                                strokeWidth = 3.0
                                stroke = Color.GRAY
                                arcWidth = 10.0
                                arcHeight = 10.0
                                strokeType = StrokeType.INSIDE
                            }
                            imageview(image)
                        }

                        stackpane {
                            text(it.name.toLowerCase().capitalize())
                        }

                        setOnDragDetected { event ->
                            val db = startDragAndDrop(TransferMode.MOVE)
                            db.dragView = image
                            val cc = ClipboardContent()
                            cc.putString(it.name)
                            db.setContent(cc)

                            event.consume()
                        }
                    }
                }
    }
}