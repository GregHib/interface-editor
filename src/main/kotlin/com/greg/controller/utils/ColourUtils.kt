package com.greg.controller.utils

import javafx.scene.paint.Color
import kotlin.math.roundToInt

object ColourUtils {

    fun colourToRS(colour: Color): Int {
        return rgbToRS((colour.red * 255).roundToInt(), (colour.green * 255).roundToInt(), (colour.blue * 255).roundToInt())
    }

    private fun rgbToRS(red: Int, green: Int, blue: Int): Int {
        return blue or (green shl 8) or (red shl 16)
    }

    private fun rsToColour(colour: Int): Color {
        val red = colour shr 16 and 0xff
        val green = colour shr 8 and 0xff
        val blue = colour and 0xff
        return Color(red / 255.0, green / 255.0, blue / 255.0, 1.0)
    }

    fun toRgba(colour: Int, alpha: Int = 255): Int {
        val r = (colour shr 16 and 0xff)
        val g = (colour shr 8 and 0xff)
        val b = (colour and 0xff)
        return alpha shl 24 or (r shl 16) or (g shl 8) or b
    }
}