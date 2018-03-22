package com.greg.model.cache.widgets

import com.greg.model.settings.Settings
import io.nshusa.rsam.binary.Widget
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.TableCell
import javafx.scene.image.ImageView
import tornadofx.rowItem
import java.awt.image.BufferedImage

class WidgetListCell : TableCell<Widget, BufferedImage>() {

    private var image: ImageView? = null
    private var id = 0

    override fun updateItem(item: BufferedImage?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            graphic = null
        } else {
            if(image == null || id != rowItem.id) {
                val fxImage = SwingFXUtils.toFXImage(rowItem.toBufferedImage(), null)
                val view = ImageView(fxImage)
                view.isPreserveRatio = true
                if(fxImage.width > Settings.getDouble(Settings.DEFAULT_WIDGET_LIST_IMAGE_WIDTH))
                    view.fitWidth = Settings.getDouble(Settings.DEFAULT_WIDGET_LIST_IMAGE_WIDTH)
                if(fxImage.height > Settings.getDouble(Settings.DEFAULT_WIDGET_LIST_IMAGE_HEIGHT))
                    view.fitHeight = Settings.getDouble(Settings.DEFAULT_WIDGET_LIST_IMAGE_HEIGHT)
                image = view
                id = rowItem.id
            }
            graphic = image
        }
    }
}