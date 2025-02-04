package com.moare.android.core.di

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.translate.AmazonTranslateClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AWSModule {
    @Provides
    @Singleton
    fun provideCredentialsProvider(@ApplicationContext context: Context): CognitoCachingCredentialsProvider {
        return CognitoCachingCredentialsProvider(
            context,
            "ap-northeast-2:efa201e1-412b-438a-927f-411cc4838469",
            Regions.AP_NORTHEAST_2
        )
    }

    @Provides
    @Singleton
    fun provideTranslateClient(credentialsProvider: CognitoCachingCredentialsProvider): AmazonTranslateClient {
        return AmazonTranslateClient(credentialsProvider)
    }

    @Provides
    @Singleton
    fun provideS3Client(credentialsProvider: CognitoCachingCredentialsProvider): AmazonS3Client {
        return AmazonS3Client(credentialsProvider, Region.getRegion(Regions.AP_NORTHEAST_2))
    }

    @Provides
    @Singleton
    fun provideTransferUtility(@ApplicationContext context: Context, s3Client: AmazonS3Client): TransferUtility {
        return TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .build()
    }
}