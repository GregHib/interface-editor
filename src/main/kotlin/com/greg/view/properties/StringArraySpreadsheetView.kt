package com.greg.view.properties

import com.greg.model.widgets.properties.extended.ObjProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.SelectionMode
import org.controlsfx.control.spreadsheet.*

class StringArraySpreadsheetView(propertyItem: RangePropertyItem) : SpreadsheetView() {
    private val property = propertyItem.prop() as ObjProperty<Array<String>>
    private val range = propertyItem.propertyRange
    private val array = property.value.clone()

    private val listener = ChangeListener<Any> { observable, _, newValue ->
        val cell = observable as ObjectProperty<Any>
        val bean = cell.bean as SpreadsheetCellBase

        if(bean is CustomSpreadSheetCell<*>) {
            array[bean.index] = newValue as? String ?: ""

            property.set(array)
        }
    }

    private fun refresh() {
        val rowCount = range.value.last
        val columnCount = range.value.first

        val grid = GridBase(rowCount, columnCount)

        grid.columnHeaders.setAll((0 until columnCount).map { it.toString() })

        buildGrid(grid, property.value as Array<String>, rowCount, columnCount)

        setGrid(grid)
    }

    init {
        refresh()
        range.addListener { _, _, _ -> refresh() }
        selectionModel.selectionMode = SelectionMode.SINGLE
    }

    private fun buildGrid(grid: GridBase, array: Array<String>, width: Int, height: Int) {
        val cells = FXCollections.observableArrayList<ObservableList<SpreadsheetCell>>()

        var index = 0
        for (column in 0 until height) {
            val randomRow = FXCollections.observableArrayList<SpreadsheetCell>()
            for (row in 0 until width) {
                val cell = CustomSpreadSheetCell(row, column, array[index++], SpreadsheetCellType.STRING, index)
                randomRow.add(cell)
            }
            cells.add(randomRow)
        }
        grid.setRows(cells)
        cells.forEach { it.forEach { cell -> cell.itemProperty().addListener(listener) } }
    }

    fun setValueProperty(value: Array<String>) {
        property.set(value)
    }

    fun getValueProperty(): ObservableValue<Array<String>> {
        return property
    }

}