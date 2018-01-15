package com.greg.controller.view

import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import tornadofx.*

class PanelGroup(label: String, separator: Boolean = true) : View() {

    private val list = mutableListOf<PanelRow>()
    private val box = vbox()

    override val root = vbox {
        prefWidth = 276.0
        label(label) {
            padding = Insets(5.0, 0.0, 5.0, 0.0)
            alignment = Pos.CENTER
            prefWidth = 276.0
        }

        if(separator)
            separator { orientation = Orientation.HORIZONTAL }

        add(box)
    }

    fun addRow(row: PanelRow) {
        box.add(row)
        list.add(row)
    }

    fun clearRows() {
        box.clear()
        list.clear()
    }

    fun rows(): ObservableList<PanelRow> {
        return list.observable()
    }
}