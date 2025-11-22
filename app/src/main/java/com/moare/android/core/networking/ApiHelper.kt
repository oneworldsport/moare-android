package com.moare.android.core.networking

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moare.android.core.networking.apiendpoint.AuthApi
import com.moare.android.core.networking.apiendpoint.CognitoAuthApi
import com.moare.android.core.networking.apiendpoint.KeywordsApi
import com.moare.android.core.networking.apiendpoint.SearchApi
import com.moare.android.features.moat.networking.MoatApi
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.userprofile.networking.UserProfileApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
//import com.moare.android.features.search.models.DataModelDeserializer
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiHelper @Inject constructor() {
//    private val gson = GsonBuilder()
//        .registerTypeAdapter(DataModel::class.java, DataModelDeserializer())
//        .create()
//
//    private val searchRetrofit = Retrofit.Builder()
//        .baseUrl("http://10.0.2.2:8000/search/")
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build()

//    val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)    // 연결 시도 시간
//        .readTimeout(30, TimeUnit.SECONDS)       // 서버로부터 응답 읽는 시간
//        .writeTimeout(30, TimeUnit.SECONDS)      // 서버로 데이터 보내는 시간
//        .build()
}