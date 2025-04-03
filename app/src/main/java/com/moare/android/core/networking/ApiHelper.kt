package com.moare.android.core.networking

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moare.android.core.networking.apiendpoint.KeywordsApi
import com.moare.android.core.networking.apiendpoint.SearchApi
import com.moare.android.features.search.models.DataModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
//import com.moare.android.features.search.models.DataModelDeserializer
import retrofit2.Retrofit

class ApiHelper {
//    private val gson = GsonBuilder()
//        .registerTypeAdapter(DataModel::class.java, DataModelDeserializer())
//        .create()
//
//    private val searchRetrofit = Retrofit.Builder()
//        .baseUrl("http://10.0.2.2:8000/search/")
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    private val searchRetrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/") // local test
//        .baseUrl("https://moare.kr/") // beanstalk
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val searchApi: SearchApi = searchRetrofit.create(SearchApi::class.java)
    val keywordsApi: KeywordsApi = searchRetrofit.create(KeywordsApi::class.java)
}