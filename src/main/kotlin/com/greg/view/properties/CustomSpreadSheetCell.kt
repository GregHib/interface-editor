package com.greg.view.properties

import org.controlsfx.control.spreadsheet.SpreadsheetCellBase
import org.controlsfx.control.spreadsheet.SpreadsheetCellType

class CustomSpreadSheetCell<T>(row: Int, column: Int, value: T, type: SpreadsheetCellType<T>, val index: Int) : SpreadsheetCellBase(row, column, 1, 1, type) {
    init {
        item = value
    }
}