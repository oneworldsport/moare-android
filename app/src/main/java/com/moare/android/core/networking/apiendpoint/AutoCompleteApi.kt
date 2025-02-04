package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.AutoComplete
import retrofit2.http.GET

interface AutoCompleteApi {
    @GET("autocomplete-data/autocomplete.json")
    suspend fun getAutoCompleteData(
    ): AutoComplete
}