package com.moare.android.features.search.models.models.football

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBPerson(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("photo") private val _photo: String? = null
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val photo: String
        get() = _photo ?: ""
}

@Serializable
data class FBTeamInfo(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("logo") private val _logo: String? = null,
    @SerialName("winner") private val _winner: Boolean? = null,
    @SerialName("update") private val _update: String? = null,
    @SerialName("code") private val _code: String? = null,
    @SerialName("country") private val _country: String? = null,
    @SerialName("founded") private val _founded: Int? = null,
    @SerialName("national") private val _national: Boolean? = null,
    val colors: FBGameColors? = null
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val logo: String
        get() = _logo ?: ""

    val winner: Boolean
        get() = _winner ?: false

    val update: String
        get() = _update ?: ""

    val code: String
        get() = _code ?: ""

    val country: String
        get() = _country ?: ""

    val founded: Int
        get() = _founded ?: 0

    val national: Boolean
        get() = _national ?: false
}

@Serializable
data class FBVenue(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("address") private val _address: String? = null,
    @SerialName("city") private val _city: String? = null,
    @SerialName("capacity") private val _capacity: Int? = null,
    @SerialName("surface") private val _surface: String? = null,
    @SerialName("image") private val _image: String? = null
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val address: String
        get() = _address ?: ""

    val city: String
        get() = _city ?: ""

    val capacity: Int
        get() = _capacity ?: 0

    val surface: String
        get() = _surface ?: ""

    val image: String
        get() = _image ?: ""
}

@Serializable
data class FBLeague(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("country") private val _country: String? = null,
    @SerialName("logo") private val _logo: String? = null,
    @SerialName("flag") private val _flag: String? = null,
    @SerialName("season") private val _season: Int? = null,
    @SerialName("round") private val _round: String? = null
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val country: String
        get() = _country ?: ""

    val logo: String
        get() = _logo ?: ""

    val flag: String
        get() = _flag ?: ""

    val season: Int
        get() = _season ?: 0

    val round: String
        get() = _round ?: ""
}

@Serializable
data class FBHomeAwayIntStats(
    @SerialName("home") private val _home: Int? = null,
    @SerialName("away") private val _away: Int? = null,
    @SerialName("total") private val _total: Int? = null
) {
    val home: Int
        get() = _home ?: 0

    val away: Int
        get() = _away ?: 0

    val total: Int
        get() = _total ?: 0
}

@Serializable
data class FBHomeAwayStringStats(
    @SerialName("home") private val _home: String? = null,
    @SerialName("away") private val _away: String? = null,
    @SerialName("total") private val _total: String? = null
) {
    val home: String
        get() = _home ?: ""

    val away: String
        get() = _away ?: ""

    val total: String
        get() = _total ?: ""
}