package com.greg.view.sprites.external

import com.greg.model.widgets.WidgetType
import com.greg.view.sprites.SpriteController
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.ListCell
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import tornadofx.View
import tornadofx.listview
import java.io.IOException

class ExternalSpriteView : View() {

    private var displayedImage: Image? = null
    override val root = listview<ExternalSprite> {
        selectionModel.selectionMode = SelectionMode.MULTIPLE


        setOnDragDetected { event ->

            val target = this.selectionModel.selectedItem
            if(target is ExternalSprite) {
                val db = startDragAndDrop(TransferMode.MOVE)
                db.dragView = SwingFXUtils.toFXImage(target.sprite.toBufferedImage(), null)
                val cc = ClipboardContent()
                cc.putString("${WidgetType.SPRITE.name}:${target.id}")
                db.setContent(cc)
            }

            event.consume()
        }
    }

    init {
        root.items = SpriteController.filteredExternal

        root.setCellFactory({ _ ->
            object : ListCell<ExternalSprite?>() {
                private val imageView = ImageView()

                override fun updateItem(sprite: ExternalSprite?, empty: Boolean) {
                    super.updateItem(sprite, empty)

                    if (empty) {
                        text = ""
                        graphic = null
                    } else {

                        try {
                            if (sprite == null || sprite.isEmpty) {
                                imageView.image = SpriteController.placeholderIcon
                                imageView.fitWidth = 32.0
                                imageView.fitHeight = 32.0
                                text = sprite?.id.toString()
                                graphic = imageView
                                return
                            }

                            val image = sprite.sprite.toBufferedImage()
                            imageView.fitWidth = (if (image.width > 128) 128.0 else image.width.toDouble())
                            imageView.fitHeight = (if (image.height > 128) 128.0 else image.height.toDouble())
                            imageView.isPreserveRatio = true
                            displayedImage = SwingFXUtils.toFXImage(image, null)
                            imageView.image = displayedImage
                            text = sprite.id.toString()
                            graphic = imageView
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }
}