package com.greg.view.canvas.widgets

import com.greg.controller.utils.ColourUtils
import com.greg.model.cache.CacheController
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

    init {
        label.textFill = Settings.getColour(Settings.DEFAULT_TEXT_DEFAULT_COLOUR)
        label.alignment = Pos.TOP_LEFT
        label.textAlignment = TextAlignment.LEFT
        label.isWrapText = true

        add(label)
        label.prefWidthProperty().bindBidirectional(outline.widthProperty())
        label.prefHeightProperty().bindBidirectional(outline.heightProperty())
        label.isVisible = false

        add(image)
    }

    fun updateColour(widget: WidgetText, forceSecondary: Boolean = false) {
        val colour = if (forceSecondary)
            if (widget.isHovered() && widget.getSecondaryHoverColour() != Color.BLACK) widget.getSecondaryHoverColour() else widget.getSecondaryColour()
        else
            if (widget.isHovered() && widget.getDefaultHoverColour() != Color.BLACK) widget.getDefaultHoverColour() else widget.getDefaultColour()

        label.textFill = colour
    }


    fun updateText(widget: WidgetText, forceSecondary: Boolean = false, cache: CacheController) {
        val text = if (forceSecondary) widget.getSecondaryText() else widget.getDefaultText()
        label.text = text
        if(cache.loaded) {
            val font = cache.fonts.fonts[widget.getFontIndex()]
            val bi = font.getAsImage(text, outline.width.toInt(), outline.height.toInt(), widget.hasShadow(), widget.isCentred(), ColourUtils.colourToRS(label.textFill as Color))
            image.image = resample(bi, 1)
        }
    }
}