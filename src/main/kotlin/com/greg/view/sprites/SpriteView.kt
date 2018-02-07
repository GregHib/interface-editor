package com.greg.view.sprites

import com.greg.model.Sprite
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.ListCell
import javafx.scene.control.SelectionMode
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import tornadofx.View
import tornadofx.listview
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO

class SpriteView : View() {

    private var displayedImage: Image? = null
    override val root = listview<Sprite> {
        selectionModel.selectionMode = SelectionMode.MULTIPLE
    }

    init {
        root.items = SpriteController.filteredList

        root.setCellFactory({ _ ->
            object : ListCell<Sprite>() {
                private val imageView = ImageView()

                override fun updateItem(sprite: Sprite?, empty: Boolean) {
                    super.updateItem(sprite, empty)

                    if (empty) {
                        text = ""
                        graphic = null
                    } else {

                        try {
                            if (sprite?.data?.isEmpty()!!) {
                                imageView.image = SpriteController.placeholderIcon
                                imageView.fitWidth = 32.0
                                imageView.fitHeight = 32.0
                                text = sprite.id.toString()
                                graphic = imageView
                                return
                            }

                            val image = ImageIO.read(ByteArrayInputStream(sprite.data))
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