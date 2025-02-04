package com.moare.android.core.di

import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.search.networking.SearchClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideApiHelper(): ApiHelper = ApiHelper()

    @Provides
    @Singleton
    fun provideSearchClient(apiHelper: ApiHelper): SearchClient = SearchClient(apiHelper)
}