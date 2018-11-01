package com.greg.model.widgets.properties

class IntValues(var first: Int, var last: Int) {
    companion object {
        val EMPTY = IntValues(0, 0)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is IntValues)
            first == other.first && last == other.last
        else
            super.equals(other)
    }
}