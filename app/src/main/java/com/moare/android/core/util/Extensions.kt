package com.moare.android.core.util

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.rounded(decimalPlaces: Int): Double {
    val multiplier = 10.0.pow(decimalPlaces)
    return (this * multiplier).roundToInt() / multiplier
}

fun Int.percentageOf(total: Int, decimalPlaces: Int): Double {
    if (total == 0) return 0.0
    val percentage = (this.toDouble() / total) * 100
    return percentage.rounded(decimalPlaces)
}