package com.greg.canvas.edit.resize

import javafx.scene.Cursor

enum class ResizeDirection(val cursor: Cursor, vararg val directions: Direction) {
    NORTH_WEST(Cursor.NW_RESIZE, Direction.NORTH, Direction.WEST),
    NORTH(Cursor.N_RESIZE, Direction.NORTH),
    NORTH_EAST(Cursor.NE_RESIZE, Direction.NORTH, Direction.EAST),
    WEST(Cursor.W_RESIZE, Direction.WEST),
    EAST(Cursor.E_RESIZE, Direction.EAST),
    SOUTH_WEST(Cursor.SW_RESIZE, Direction.SOUTH, Direction.WEST),
    SOUTH(Cursor.S_RESIZE, Direction.SOUTH),
    SOUTH_EAST(Cursor.SE_RESIZE, Direction.SOUTH, Direction.EAST);
}