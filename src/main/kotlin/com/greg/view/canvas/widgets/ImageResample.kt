package com.greg.view.canvas.widgets

import com.greg.model.settings.Settings
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import java.awt.image.BufferedImage

interface ImageResample {
    fun resample(bufferedImage: BufferedImage?, scale: Int = 10): Image? {
        return if(bufferedImage != null) if(Settings.getBoolean(Settings.SPRITE_RESAMPLING) && scale > 1) resample(SwingFXUtils.toFXImage(bufferedImage, null), scale) else SwingFXUtils.toFXImage(bufferedImage, null) else null
    }

    private fun resample(input: Image, scaleFactor: Int): Image {
        val w = input.width.toInt()
        val h = input.height.toInt()

        val output = WritableImage(w * scaleFactor, h * scaleFactor)

        val reader = input.pixelReader
        val writer = output.pixelWriter

        for (y in 0 until h) {
            for (x in 0 until w) {
                val argb = reader.getArgb(x, y)

                //Scale
                for (dy in 0 until scaleFactor) {
                    for (dx in 0 until scaleFactor) {
                        writer.setArgb(x * scaleFactor + dx, y * scaleFactor + dy, argb)
                    }
                }
            }
        }

        return output
    }
}