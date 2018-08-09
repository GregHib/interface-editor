package com.greg.controller.utils

object MathUtils {
    fun constrain(value: Int, min: Int, max: Int): Int {
        return Math.min(Math.max(value, min), max)
    }
}