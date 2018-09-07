package com.greg.view

import com.greg.model.widgets.properties.extended.ObjProperty
import com.greg.view.properties.IntArraySpreadsheetView
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.SelectionMode
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCell
import org.controlsfx.control.spreadsheet.SpreadsheetCellType
import org.controlsfx.control.spreadsheet.SpreadsheetView
import tornadofx.App
import tornadofx.View
import tornadofx.stackpane

class SpreadsheetViewTest : View() {

    override val root = stackpane {
        val intArray = ObjProperty("intArray", intArrayOf(1, 4, 8, 6, 2, 4, 3, 7, 5, 4, 2))
        val spreadSheetView = IntArraySpreadsheetView(intArray)
        add(spreadSheetView)

        spreadSheetView.grid.columnHeaders.setAll((0 until spreadSheetView.grid.columnCount).map { it.toString() })
        spreadSheetView.selectionModel.selectionMode = SelectionMode.SINGLE
    }

    inner class SpreadsheetViewExample : SpreadsheetView() {
        init {
            val rowCount = 31 //Will be re-calculated after if incorrect.
            val columnCount = 8

            val grid = GridBase(rowCount, columnCount)
            buildGrid(grid)

            setGrid(grid)
        }

        /**
         * Build the grid.
         *
         * @param grid
         */
        private fun buildGrid(grid: GridBase) {
            val rows = FXCollections.observableArrayList<ObservableList<SpreadsheetCell>>()

            for (i in 0 until 100) {
                val randomRow = FXCollections.observableArrayList<SpreadsheetCell>()
                randomRow.add(SpreadsheetCellType.INTEGER.createCell(i, 0, 1, 1, (Math.random() * 100).toInt()))
                randomRow.add(SpreadsheetCellType.INTEGER.createCell(i, 1, 1, 1, i))
                randomRow.add(SpreadsheetCellType.INTEGER.createCell(i, 2, 1, 1, (Math.random() * 100).toInt()))
                val cell = SpreadsheetCellType.DOUBLE.createCell(i, 3, 1, 1, Math.random() * 100)
                cell.format = "##.##"
                randomRow.add(cell)
                randomRow.add(SpreadsheetCellType.INTEGER.createCell(i, 4, 1, 1, (Math.random() * 2).toInt()))
                rows.add(randomRow)
            }
            grid.setRows(rows)
        }
    }
}

class HelloSpreadsheetViewApp: App(SpreadsheetViewTest::class)

fun main(args: Array<String>) {
    Application.launch(HelloSpreadsheetViewApp::class.java, *args)
}
