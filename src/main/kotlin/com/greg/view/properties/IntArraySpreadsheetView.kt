package com.greg.view.properties

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.SelectionMode
import org.controlsfx.control.spreadsheet.*

class IntArraySpreadsheetView(propertyItem: RangePropertyItem) : SpreadsheetView() {
    private val property = propertyItem.prop() as ObjProperty<IntArray>
    private val range = propertyItem.propertyRange
    private val array = property.value.clone()

    private val listener = ChangeListener<Any> { observable, _, newValue ->
        val cell = observable as ObjectProperty<Any>
        val bean = cell.bean as SpreadsheetCellBase

        if(bean is CustomSpreadSheetCell<*>) {
            println(bean.index)
            println(array.size)
            println(property.get().size)
//            array[bean.index] = newValue as Int

//            property.set(array)
        }
    }

    private fun refresh() {
        println("Refresh")
        val rowCount = range.value.last
        val columnCount = range.value.first

        val grid = GridBase(rowCount, columnCount)

        grid.columnHeaders.setAll((0 until columnCount).map { it.toString() })


        buildGrid(grid, property.value as IntArray, rowCount, columnCount)

        setGrid(grid)
    }

    init {
        selectionModel.selectionMode = SelectionMode.SINGLE
        range.addListener { _, _, _ -> refresh() }
        refresh()
    }

    /*fun getData(): IntArray {
        return grid.rows.flatMap { row -> row.map { it.item as Int } }.toIntArray()
    }*/

    private fun buildGrid(grid: GridBase, array: IntArray, width: Int, height: Int) {
        val cells = FXCollections.observableArrayList<ObservableList<SpreadsheetCell>>()

        var index = 0
        for (column in 0 until height) {
            val randomRow = FXCollections.observableArrayList<SpreadsheetCell>()
            for (row in 0 until width) {
                val cell = CustomSpreadSheetCell(row, column, array[index++], SpreadsheetCellType.INTEGER, index)
                cell.itemProperty().addListener(listener)
                randomRow.add(cell)
            }
            cells.add(randomRow)
        }
        grid.setRows(cells)
    }

    fun setValueProperty(value: IntArray) {
        property.set(value)
    }

    fun getValueProperty(): ObservableValue<IntArray> {
        return property
    }

}