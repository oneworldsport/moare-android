package com.moare.android.core.di

import com.moare.android.core.util.Trie
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrieModule {
    @Provides
    @Singleton
    fun provideTrie(): CompletableDeferred<Trie> {
        return CompletableDeferred()
    }
}