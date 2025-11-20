package com.moare.android.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.moat.networking.MoatClient
import com.moare.android.features.search.networking.SearchClient
import com.moare.android.features.sign.networking.SignClient
import com.moare.android.features.userprofile.networking.UserProfileClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
//    @Provides
//    @Singleton
//    fun provideSearchClient(
//        @ApplicationContext context: Context,
//        apiHelper: ApiHelper
//    ): SearchClient = SearchClient(context, apiHelper)
}