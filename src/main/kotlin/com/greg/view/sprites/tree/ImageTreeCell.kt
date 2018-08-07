package com.greg.view.sprites.tree

import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.TreeCell
import javafx.scene.image.Image
import tornadofx.imageview

class ImageTreeCell : TreeCell<String>() {
    companion object {
        val placeholderIcon = Image(ImageTreeCell::class.java.getResourceAsStream("../placeholder.png"))
    }

    override fun updateItem(string: String?, empty: Boolean) {
        super.updateItem(string, empty)
        val item = treeItem

        text = if (empty) null else string
        graphic = when {
            item != null && item.isLeaf && item is ImageTreeItem -> {
                //Sprite display cache loading
                if (item.imageView == null) {
                    item.imageView = imageview {
                        isPreserveRatio = true
                        //Placeholder for null images
                        if (item.sprite == null) {
                            val image = placeholderIcon
                            fitWidth = (if (image.width > 128) 128.0 else image.width)
                            fitHeight = (if (image.height > 128) 128.0 else image.height)
                            this.image = image
                        } else {
                            //Display image
                            val image = item.sprite.toBufferedImage()
                            fitWidth = (if (image.width > 128) 128.0 else image.width.toDouble())
                            fitHeight = (if (image.height > 128) 128.0 else image.height.toDouble())
                            this.image = SwingFXUtils.toFXImage(image, null)
                        }
                    }
                }
                item.imageView
            }
            else -> null
        }
    }
}