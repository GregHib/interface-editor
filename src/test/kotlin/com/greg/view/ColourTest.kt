package com.greg.view

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.roundToInt

class ColourTest : View() {

    private lateinit var rs: TextField
    private lateinit var rgb: ColorPicker

    private lateinit var rsRGB: ColorPicker

    private lateinit var rgbRS: Label

    override val root = pane {
        title = "Colour Converter"
        hbox(20) {
            padding = Insets(20.0)
            vbox(4) {
                label("RS Int: ")
                rs = textfield {
                    filterInput { it.controlNewText.isInt() }
                    setOnAction { updateRS(if (text.isEmpty()) 0 else text.toInt()) }
                }

                rsRGB = colorpicker {
                    isEditable = false
                }
            }

            vbox(4) {

                label()

                rgb = colorpicker {
                    setOnAction { updateRGB(value) }
                }

                rgbRS = label("RS Int: ")
            }
        }
    }

    private fun updateRGB(colour: Color?) {
        rgbRS.text = "RS Int: ${colourToRS(colour!!)}"
    }

    private fun updateRS(rs: Int) {
        val colour = rsToColour(rs)
        rsRGB.value = colour
    }


    private fun colourToRS(colour: Color): Int {
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
}

class ColourTestApp : App(ColourTest::class)

fun main(args: Array<String>) {
    Application.launch(ColourTestApp::class.java, *args)
}