package com.moare.android.core.networking

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moare.android.BuildConfig
import com.moare.android.core.networking.apiendpoint.KeywordsApi
import com.moare.android.core.networking.apiendpoint.SearchApi
import com.moare.android.features.search.models.DataModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
//import com.moare.android.features.search.models.DataModelDeserializer
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

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

//    val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)    // 연결 시도 시간
//        .readTimeout(30, TimeUnit.SECONDS)       // 서버로부터 응답 읽는 시간
//        .writeTimeout(30, TimeUnit.SECONDS)      // 서버로 데이터 보내는 시간
//        .build()

    val appVersionName = BuildConfig.VERSION_NAME
    val appVersionCode = BuildConfig.VERSION_CODE

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("X-Platform", "android")
                .addHeader("X-App-Version", appVersionName)
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val searchRetrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/") // local test
//        .baseUrl("https://moare.kr/") // beanstalk
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val searchApi: SearchApi = searchRetrofit.create(SearchApi::class.java)
    val keywordsApi: KeywordsApi = searchRetrofit.create(KeywordsApi::class.java)
}