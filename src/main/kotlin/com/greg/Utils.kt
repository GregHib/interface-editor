package com.greg

import kotlin.math.round

class Utils {
    companion object {
        fun constrain(value: Double, max: Double): Double {
            return constrain(value, 0.0, max)
        }

        fun constrain(value: Double, min: Double, max: Double): Double {
            return round(if (value < min) min else if (value > max) max else value)
        }
    }
}