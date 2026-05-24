package com.moare.android.core.di

import com.moare.android.features.search.data.repository.KeywordsRepository
import com.moare.android.features.search.data.repository.SearchRepository
import com.moare.android.features.search.domain.repository.DefaultKeywordsRepository
import com.moare.android.features.search.domain.repository.DefaultSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
// NOTE: 여기서 SearchRepository는 feature search의 Repository들을 의미함.
// TODO: 그럼 안에 있는 SearchRepository의 이름을 바꿔야할까..?
abstract class SearchRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: DefaultSearchRepository
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindKeywordsRepository(
        impl: DefaultKeywordsRepository
    ): KeywordsRepository
}