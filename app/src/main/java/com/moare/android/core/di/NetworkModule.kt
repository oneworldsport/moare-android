package com.moare.android.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.networking.TokenAuthenticator
import com.moare.android.core.networking.TokenInterceptor
import com.moare.android.core.networking.apiendpoint.AuthApi
import com.moare.android.core.networking.apiendpoint.CognitoAuthApi
import com.moare.android.core.networking.apiendpoint.SearchApi
import com.moare.android.features.moat.networking.MoatApi
import com.moare.android.features.moat.networking.MoatClient
import com.moare.android.features.search.networking.SearchClient
import com.moare.android.features.sign.networking.SignClient
import com.moare.android.features.userprofile.networking.UserProfileApi
import com.moare.android.features.userprofile.networking.UserProfileClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

object NetworkJson {
    val instance = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
//    @Provides
//    @Singleton
//    fun provideSearchClient(
//        @ApplicationContext context: Context,
//        apiHelper: ApiHelper
//    ): SearchClient = SearchClient(context, apiHelper)

    @Provides
    @Singleton
    @NoAuth
    fun provideNoAuthOkHttp(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @Authenticated
    fun provideAuthOkHttp(
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    @NoAuth
    fun provideNoAuthRetrofit(
        @NoAuth noAuthOkHttp: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // local test
//            .baseUrl("https://moare.kr/") // beanstalk
            .addConverterFactory(NetworkJson.instance.asConverterFactory("application/json".toMediaType()))
            .client(noAuthOkHttp)
            .build()

    @Provides
    @Singleton
    @Authenticated
    fun provideAuthRetrofit(
        @Authenticated authOkHttp: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // local test
//            .baseUrl("https://moare.kr/") // beanstalk
            .addConverterFactory(NetworkJson.instance.asConverterFactory("application/json".toMediaType()))
            .client(authOkHttp)
            .build()

    @Provides
    @Singleton
    @Cognito
    fun provideCognitoAuthRetrofit(
        @NoAuth noAuthOkHttp: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://ap-northeast-2jfrbeda09.auth.ap-northeast-2.amazoncognito.com/")
            .addConverterFactory(NetworkJson.instance.asConverterFactory("application/json".toMediaType()))
            .client(noAuthOkHttp)
            .build()

    @Provides
    @Singleton
    fun provideSearchApi(@NoAuth retrofit: Retrofit): SearchApi = retrofit.create(SearchApi::class.java)

    @Provides
    @Singleton
    @NoAuth
    fun provideAuthApi(@NoAuth retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @Authenticated
    fun provideProtectedAuthApi(@Authenticated retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @NoAuth
    fun provideMoatApi(@NoAuth retrofit: Retrofit): MoatApi = retrofit.create(MoatApi::class.java)

    @Provides
    @Singleton
    @Authenticated
    fun provideProtectedMoatApi(@Authenticated retrofit: Retrofit): MoatApi = retrofit.create(MoatApi::class.java)

    @Provides
    @Singleton
    @NoAuth
    fun provideUserProfileApi(@NoAuth retrofit: Retrofit): UserProfileApi = retrofit.create(UserProfileApi::class.java)

    @Provides
    @Singleton
    @Authenticated
    fun provideProtectedUserProfileApi(@Authenticated retrofit: Retrofit): UserProfileApi = retrofit.create(UserProfileApi::class.java)

    @Provides
    @Singleton
    fun provideCognitoAuthApi(@Cognito retrofit: Retrofit): CognitoAuthApi = retrofit.create(CognitoAuthApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authenticated

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Cognito





























