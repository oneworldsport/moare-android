package com.moare.android.core.util


object FBUtil {
    fun teamLogoUrl(id: Int?): String? {
        return id?.let { "https://media.api-sports.io/football/teams/$it.png" }
    }
}