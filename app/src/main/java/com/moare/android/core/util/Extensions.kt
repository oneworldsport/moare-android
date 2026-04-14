package com.moare.android.core.util

import android.annotation.SuppressLint
import com.moare.android.features.search.models.displaymodels.Rankable
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

@SuppressLint("DefaultLocale")
fun Double.format3(): String = String.format("%.3f", this)

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

/**
 * 이미 정렬된 상태라고 가정하고, key 값으로 공동순위 부여 (1,2,2,4 방식)
 */
fun <T : Rankable<T>, K> List<T>.withCompetitionRankBy(key: (T) -> K): List<T> {
    if (isEmpty()) return this

    var currentRank = 1
    var sameCount = 0
    var lastKey: K? = null

//    for (i in indices) {
//        val k = key(this[i])
//
//        if (lastKey == null || k != lastKey) {
//            // 값이 바뀌면: rank를 "이전 공동순위 개수만큼" 점프
//            currentRank += sameCount
//            sameCount = 1
//            lastKey = k
//        } else {
//            // 값이 같으면: 같은 rank 유지
//            sameCount += 1
//        }
//
//        this[i].displayRank = currentRank
//    }

    // NOTE: 위 로직(var displayRank를 수정하는 방식)은 compose ui에 반영이 안돼 아래처럼 변경
    return mapIndexed { index, item ->
        val k = key(item)

        if (index == 0) {
            currentRank = 1
            sameCount = 1
            lastKey = k
        } else if (k != lastKey) {
            // 값이 바뀌면: rank를 "이전 공동순위 개수만큼" 점프
            currentRank += sameCount
            sameCount = 1
            lastKey = k
        } else {
            // 값이 같으면: 같은 rank 유지
            sameCount += 1
        }

        item.withDisplayRank(currentRank)
    }
}

// TODO: 함수 다른곳에서 다시 정리 필요
fun toCm(feet: Int, inches: Int, decimalPlaces: Int = 0): Double {
    val totalInches = feet * 12 + inches
    return (totalInches * 2.54).rounded(decimalPlaces)
}

val Int?.displayOrDash: String
    get() = this?.toString() ?: "-"