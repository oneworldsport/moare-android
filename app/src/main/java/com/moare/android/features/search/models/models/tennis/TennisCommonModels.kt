package com.moare.android.features.search.models.models.tennis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TennisCountry(
    @SerialName("alpha3") private val _alpha3: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val alpha3: String get() = _alpha3 ?: ""
    val name: String get() = _name ?: ""
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisName(
    @SerialName("name") private val _name: String? = null,
) {
    val name: String get() = _name ?: ""
}