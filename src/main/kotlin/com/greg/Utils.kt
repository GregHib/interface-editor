package com.greg

class Utils {
    companion object Methods {
        fun constrain(value: Double, max: Double): Double {
            return if (value < 0.0) 0.0 else if (value > max) max else value
        }
    }
}