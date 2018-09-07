package com.greg.view.properties

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.SelectionMode
import org.controlsfx.control.spreadsheet.*
import tornadofx.onChange

class IntArraySpreadsheetView(propertyItem: PropertyItem) : SpreadsheetView() {
    private val property = propertyItem.prop() as ObjProperty<IntArray>

    private val listener = ChangeListener<Any> { observable, _, newValue ->
        val cell = observable as ObjectProperty<Any>
        val bean = cell.bean as SpreadsheetCellBase
        val array = property.value.clone()

        array[bean.row] = newValue as Int

        property.set(array)
    }

    init {
        val array = propertyItem.value as IntArray
        val rowCount = array.size //Will be re-calculated after if incorrect.
        val columnCount = 1

        val grid = GridBase(rowCount, columnCount)

        grid.columnHeaders.setAll((0 until grid.columnCount).map { it.toString() })
        selectionModel.selectionMode = SelectionMode.SINGLE

        buildGrid(grid, array)

        setGrid(grid)

        grid.rows.onChange {
            println("Change")
        }
    }

    /*fun getData(): IntArray {
        return grid.rows.flatMap { row -> row.map { it.item as Int } }.toIntArray()
    }*/

    private fun buildGrid(grid: GridBase, array: IntArray) {
        val rows = FXCollections.observableArrayList<ObservableList<SpreadsheetCell>>()

        for (i in 0 until array.size) {
            val randomRow = FXCollections.observableArrayList<SpreadsheetCell>()
            val cell = SpreadsheetCellType.INTEGER.createCell(i, 0, 1, 1, array[i])
            cell.itemProperty().addListener(listener)
            randomRow.add(cell)
            rows.add(randomRow)
        }
        grid.setRows(rows)
    }

    fun setValueProperty(value: IntArray) {
        property.set(value)
    }

    fun getValueProperty(): ObservableValue<IntArray> {
        return property
    }

}