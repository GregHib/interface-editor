package com.greg.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.paint.Color
import org.controlsfx.control.PropertySheet
import tornadofx.*
import tornadofx.Stylesheet.Companion.sheet
import tornadofx.controlsfx.propertysheet

class RightPane : Fragment() {

    val number = SimpleIntegerProperty(this, "number", 500)
    val sheet = PropertySheet()
    override val root = drawer {
        item("Properties", expanded = true) {
            vbox {
                add(sheet)

//                sheet.items.add(PropertyItem("Text", "Basic", "Some text"))
//                sheet.items.add(PropertyItem("Boolean", "Misc", false))
                sheet.items.add(PropertyItem("Number", "Misc", number))
//                sheet.items.add(PropertyItem("Colour", "Misc", Color.CADETBLUE))
            }
        }
        item("CS2 Editor") {
        }
    }

    val onSelection: (String) -> Unit = {
        sheet.items.clear()
    }
}