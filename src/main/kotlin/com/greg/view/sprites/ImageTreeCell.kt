package com.greg.view.sprites

import javafx.scene.control.TreeCell
import tornadofx.imageview

class ImageTreeCell : TreeCell<String>() {

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)

        text = if (empty) null else item
        val itm = treeItem
        graphic = when {
            empty -> null
            itm.isLeaf && itm is ImageTreeItem -> {
                if (itm.imageView == null) {
                    itm.imageView = imageview {
                        image = itm.image
                        fitWidth = (if (image.width > 128) 128.0 else image.width)
                        fitHeight = (if (image.height > 128) 128.0 else image.height)
                        isPreserveRatio = true
                    }
                }
                itm.imageView
            }
            else -> null
        }
    }
}