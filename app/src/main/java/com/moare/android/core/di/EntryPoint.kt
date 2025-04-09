package com.moare.android.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil3.ImageLoader
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.translate.AmazonTranslateClient
import com.moare.android.core.util.Trie
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.TrendingKeywords
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred

@EntryPoint
@InstallIn(SingletonComponent::class) // NOTE: 앱 전체 생명주기 동안 1개만 생성됨
interface EntryPoint {
    fun getTranslateClient(): AmazonTranslateClient
    fun getS3Client(): AmazonS3Client
    fun getTransferUtility(): TransferUtility

    fun getDataStore(): DataStore<Preferences>

    fun imageLoader(): ImageLoader

    fun getTranslatedNameProvider(): TranslatedNameProvider

    fun getTrieDeferred(): CompletableDeferred<Pair<Trie, List<KeywordInfo>>>
    fun getNotice(): CompletableDeferred<List<NoticeModel>>
    fun getTrendingKeywords(): CompletableDeferred<TrendingKeywords>
}