package com.moare.android.features.search.models.models.mlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBNameObj(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("link") private val _link: String? = null,
    @SerialName("name") private val _name: String? = null,
) {
    val id: Int get() = _id ?: 0
    val link: String get() = _link ?: ""
    val name: String get() = _name ?: ""
}

@Serializable
data class MLBFullNameObj(
    @SerialName("fullName") private val _fullName: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("link") private val _link: String? = null,
) {
    val fullName: String get() = _fullName ?: ""
    val id: Int get() = _id ?: 0
    val link: String get() = _link ?: ""
}


@Serializable
data class MLBCodeObj(
    @SerialName("code") private val _code: String? = null,
    @SerialName("description") private val _description: String? = null,
) {
    val code: String get() = _code ?: ""
    val description: String get() = _description ?: ""
}

@Serializable
data class MLBAbbreviationIdObj(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("link") private val _link: String? = null,
    @SerialName("name") private val _name: String? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val id: Int get() = _id ?: 0
    val link: String get() = _link ?: ""
    val name: String get() = _name ?: ""
}

@Serializable
data class MLBAbbreviationCodeObj(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("code") private val _code: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("type") private val _type: String? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val code: String get() = _code ?: ""
    val name: String get() = _name ?: ""
    val type: String get() = _type ?: ""
}

@Serializable
data class MLBLabelObj(
    @SerialName("label") private val _label: String? = null,
    @SerialName("value") private val _value: String? = null,
) {
    val label: String get() = _label ?: ""
    val value: String get() = _value ?: ""
}