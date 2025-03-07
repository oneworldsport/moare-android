package com.moare.android.features.search.models

sealed class ApiFetchState {
    data object Idle : ApiFetchState()
    data object Fetching : ApiFetchState()
    data object Success : ApiFetchState()
    data class Error(val message: String) : ApiFetchState()
}