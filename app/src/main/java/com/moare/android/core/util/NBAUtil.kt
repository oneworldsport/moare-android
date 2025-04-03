package com.moare.android.core.util

object NBAUtil {
//    val nbaLogoUrl = "https://cdn.nba.com/logos/leagues/logo-nba.svg"

    fun translateEastWest(input: String): String {
        return if (input.lowercase() == "east") {
            "동부"
        } else if (input.lowercase() == "west") {
            "서부"
        } else {
            input
        }
    }
}