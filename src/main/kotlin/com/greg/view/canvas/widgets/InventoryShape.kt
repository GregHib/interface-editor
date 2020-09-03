package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveMedia
import com.greg.model.widgets.properties.IntValues
import com.greg.model.widgets.type.WidgetInventory
import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.shape.StrokeType
import tornadofx.add

class InventoryShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    val group = Group()

    init {
        add(group)
    }

    fun updateInventory(widget: WidgetInventory) {
        var item = 0

        widget.arrayRange.set(IntValues(widget.getSlotWidth(), widget.getSlotHeight()))

        group.children.clear()

        for (childY in 0 until widget.getSlotHeight()) {
            for (childX in 0 until widget.getSlotWidth()) {
                var componentX = childX * (32 + widget.getSpritePaddingX())
                var componentY = childY * (32 + widget.getSpritePaddingY())

                if (item < 20) {
                    componentX += widget.getSpriteX()[item]
                    componentY += widget.getSpriteY()[item]
                }

                if (item < 20) {
                    val image = ArchiveMedia.getImage(widget.getSpritesArchive()[item], widget.getSpritesIndex()[item])
                    if (image != null) {
                        val view = displayImage(ImageView(), image, outline)
                        view.translateX = componentX.toDouble() //+ sprite.offsetX
                        view.translateY = componentY.toDouble() //+ sprite.offsetY
                        group.add(view)
                    }
                }

                //Basic outline
                val rectangle = Rectangle(componentX.toDouble(), componentY.toDouble(), 32.0, 32.0)
                rectangle.stroke = Color.BLACK
                rectangle.fill = Color.TRANSPARENT
                rectangle.strokeType = StrokeType.INSIDE
                group.add(rectangle)

                item++
            }
        }
        widget.setWidth(widget.getSlotWidth() * (32 + widget.getSpritePaddingX()))
        widget.setHeight(widget.getSlotHeight() * (32 + widget.getSpritePaddingY()))
    }
}