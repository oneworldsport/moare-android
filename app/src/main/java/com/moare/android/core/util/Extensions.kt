package com.moare.android.core.util

import kotlin.math.pow
import kotlin.math.roundToInt

// 반올림해서 decimalPlaces자리까지 표시
fun Double.rounded(decimalPlaces: Int): Double {
    val multiplier = 10.0.pow(decimalPlaces)
    return (this * multiplier).roundToInt() / multiplier
}

fun Int.percentageOf(total: Int, decimalPlaces: Int): Double {
    if (total == 0) return 0.0
    val percentage = (this.toDouble() / total) * 100
    return percentage.rounded(decimalPlaces)
}

fun Int.toKg(decimalPlaces: Int = 0): Double = (this * 0.453592).rounded(decimalPlaces)
fun Double.toKg(decimalPlaces: Int = 0): Double = (this * 0.453592).rounded(decimalPlaces)

/**
 * Use when only (possible)last name need
 */
fun String.dropFirstWord(): String {
    val components = this.split(" ")
    return if (components.size > 1) {
        components.drop(1).joinToString(" ")
    } else {
        this
    }
}

// TODO: 함수 다른곳에서 다시 정리 필요
fun toCm(feet: Int, inches: Int, decimalPlaces: Int = 0): Double {
    val totalInches = feet * 12 + inches
    return (totalInches * 2.54).rounded(decimalPlaces)
}

val Int?.displayOrDash: String
    get() = this?.toString() ?: "-"