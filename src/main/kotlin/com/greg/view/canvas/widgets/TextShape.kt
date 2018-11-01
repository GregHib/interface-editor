package com.greg.view.canvas.widgets

import com.greg.controller.utils.ColourUtils
import com.greg.model.cache.CacheController
import com.greg.model.cache.archives.ArchiveFont
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.WidgetText
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.add

class TextShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    val image = ImageView()
    val label = Label(Settings.get(Settings.DEFAULT_TEXT_MESSAGE))
    var flip = false

    init {
        label.textFill = Settings.getColour(Settings.DEFAULT_TEXT_DEFAULT_COLOUR)
        label.alignment = Pos.TOP_LEFT
        label.textAlignment = TextAlignment.LEFT
        label.isWrapText = true
        label.font = ArchiveFont.small

        add(label)
        label.prefWidthProperty().bindBidirectional(outline.widthProperty())
        label.prefHeightProperty().bindBidirectional(outline.heightProperty())
        label.isVisible = false

        add(image)
    }

    fun updateColour(widget: WidgetText) {
        val colour = if (flip)
            if (widget.isHovered() && widget.getSecondaryHoverColour() != Color.BLACK) widget.getSecondaryHoverColour() else widget.getSecondaryColour()
        else
            if (widget.isHovered() && widget.getDefaultHoverColour() != Color.BLACK) widget.getDefaultHoverColour() else widget.getDefaultColour()

        label.textFill = colour
    }


    fun updateText(widget: WidgetText, cache: CacheController) {
        val text = if (flip && widget.getSecondaryText().isNotEmpty()) widget.getSecondaryText() else widget.getDefaultText()
        label.text = text
        if(cache.loaded) {
            label.isVisible = false
            val font = cache.fonts.fonts[widget.getFontIndex()]
            val bi = font.getAsImage(text, widget.hasShadow(), widget.isCentred(), ColourUtils.colourToRS(label.textFill as Color))
            image.image = resample(bi, 1)
        } else {
            label.isVisible = true
            label.effect = if(widget.hasShadow()) ArchiveFont.shadow else null
            //Only supports the 4 basic fonts
            label.font = when(widget.getFontIndex()) {
                1 -> ArchiveFont.medium
                2 -> ArchiveFont.bold
                3 -> ArchiveFont.thin
                else -> ArchiveFont.small
            }
        }
    }
}