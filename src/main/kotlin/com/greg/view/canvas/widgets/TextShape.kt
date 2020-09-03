package com.greg.view.canvas.widgets

import com.greg.model.cache.archives.ArchiveFont
import com.greg.model.settings.Settings
import com.greg.model.widgets.type.WidgetText
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.text.TextAlignment
import tornadofx.add

class TextShape(id: Int, width: Int, height: Int) : WidgetShape(id, width, height), ImageResample {

    val label = Label(Settings.get(Settings.DEFAULT_TEXT_MESSAGE))

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
    }

    fun updateColour(widget: WidgetText) {
        val colour = widget.getColour()

        label.textFill = colour
    }

    fun updateText(widget: WidgetText) {
        val text = widget.getText()
        label.text = text
        label.isVisible = true
        label.effect = if (widget.isShaded()) ArchiveFont.shadow else null
        label.alignment = getAlignment(widget.horizontalAlign, widget.verticalAlign)
        label.font = when (widget.getFont()) {
            496 -> ArchiveFont.medium
            497 -> ArchiveFont.bold
            498 -> ArchiveFont.thin
            else -> ArchiveFont.small
        }
    }
    private fun getAlignment(horizontal: Number?, vertical: Number?): Pos? {
        return when {
            horizontal == 0 && vertical == 0 -> Pos.BOTTOM_LEFT
            horizontal == 0 && vertical == 1 -> Pos.CENTER_LEFT
            horizontal == 0 && vertical == 2 -> Pos.TOP_LEFT
            horizontal == 1 && vertical == 0 -> Pos.BOTTOM_CENTER
            horizontal == 1 && vertical == 1 -> Pos.CENTER
            horizontal == 1 && vertical == 2 -> Pos.TOP_CENTER
            horizontal == 2 && vertical == 0 -> Pos.BOTTOM_RIGHT
            horizontal == 2 && vertical == 1 -> Pos.CENTER_RIGHT
            horizontal == 2 && vertical == 2 -> Pos.TOP_RIGHT
            else -> Pos.BASELINE_CENTER
        }
    }
}