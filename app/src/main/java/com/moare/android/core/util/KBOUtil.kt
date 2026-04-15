package com.moare.android.core.util

import com.moare.android.features.search.models.models.nba.NBAGameSummary

object KBOUtil {
    val codeMap = mapOf(
        1 to "OB",
        2 to "LT",
        3 to "SS",
        4 to "HH",
        5 to "LG",
        6 to "HT",
        7 to "WO",
        8 to "NC",
        9 to "KT",
        10 to "SK"
    )
    val kboLogoUrl = "https://6ptotvmi5753.edge.naverncp.com/KBO_IMAGE/KBOHome/resources/images/common/h1_logo.png"

    fun playerPhotoUrl(season: Int, id: Int?): String? {
        return id?.let { "https://6ptotvmi5753.edge.naverncp.com/KBO_IMAGE/person/middle/$season/$id.jpg" }
    }

    fun teamLogoUrl(id: Int?): String? {
        return codeMap[id]?.let { "https://6ptotvmi5753.edge.naverncp.com/KBO_IMAGE/emblem/regular/fixed/emblem_$it.png" }
    }

    fun getFullYear(fromYear: String): Int {
        val num = fromYear.filter { it.isDigit() }.toIntOrNull() ?: return 2025
        return 2000 + num
    }

    fun calculateYear(fromYear: String): Int {
        return 2025 - getFullYear(fromYear) + 1
    }

    fun formatMoney(input: String): String {
        val num = input.filter { it.isDigit() }.toIntOrNull() ?: return input

        val eok = num / 10000           // 억 단위
        val cheon = (num % 10000) / 1000  // 천 단위

        return when {
            eok > 0 && cheon > 0 -> "${eok}억 ${cheon}천만원"
            eok > 0 -> "${eok}억원"
            cheon > 0 -> "${cheon}천만원"
            else -> "${num}만원"
        }
    }
}