package com.moare.android.features.search.models

sealed class SearchDataState {
    object Idle : SearchDataState()
    object Fetching : SearchDataState()
    object Success : SearchDataState()
    data class Error(val message: String) : SearchDataState()
}