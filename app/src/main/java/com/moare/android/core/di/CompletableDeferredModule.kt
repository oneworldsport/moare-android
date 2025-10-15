package com.moare.android.core.di

import com.moare.android.core.util.Trie
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.TrendingKeywords
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompletableDeferredModule {
    @Provides
    @Singleton
//    @Named("Test") // Return type 같을때 사용
    // 사용할때: @Named("Test") 붙이면 됨
//    @Test // Return type 같을때 사용
    fun provideTrie(): CompletableDeferred<Pair<Trie, List<KeywordInfo>>> {
        return CompletableDeferred()
    }

    @Provides
    @Singleton
    fun provideNotice(): CompletableDeferred<List<NoticeModel>> {
        return CompletableDeferred()
    }

    @Provides
    @Singleton
    fun provideTrendingKeywords(): CompletableDeferred<TrendingKeywords> {
        return CompletableDeferred()
    }

    @Provides
    @Singleton
    fun provideTournamentTeams(): CompletableDeferred<Map<String, List<Int?>>> {
        return CompletableDeferred()
    }
}

// Return type 같을때 사용
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class Test
// 사용할때: @Test 붙이면 됨