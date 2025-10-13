package com.moare.android.core.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.ObjectMetadata
import com.moare.android.core.di.EntryPoint
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.TrendingKeywords
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.File

object AWSUtils {
    private val AUTOCOMPLETE_ETAG_KEY = stringPreferencesKey("autoCompleteETag")
    private val NBA_PLAYER_NAME_DICTIONARY_ETAG_KEY = stringPreferencesKey("nbaPlayerNameDictionaryETag")

    // TODO: 함수 기능 겹쳐서 합칠 수 있을듯
    suspend fun checkTrendingKeywords(
        context: Context,
        s3Key: String,
        eTagKey: Preferences.Key<String>
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
        val s3Client = entryPoint.getS3Client()
        val transferUtility = entryPoint.getTransferUtility()
        val dataStore = entryPoint.getDataStore()
        val trendingKeywordsDeferred = entryPoint.getTrendingKeywords()

        val bucket = "sport-search-engine"
        val objectMetadata: ObjectMetadata = withContext(Dispatchers.IO) {
            s3Client.getObjectMetadata(bucket, s3Key)
        }
        val newETag = objectMetadata.eTag

        val currentETag = dataStore.data
            .map { preferences -> preferences[eTagKey] ?: "" }
            .first()

        if (newETag == currentETag) {
            val jsonString = withContext(Dispatchers.IO) {
                File(context.filesDir, s3Key.substringAfter("/")).readText()
            }
            val jsonElement = Json.parseToJsonElement(jsonString)
            val trendingKeywords: TrendingKeywords = Json.decodeFromJsonElement(jsonElement)
            trendingKeywordsDeferred.complete(trendingKeywords)

            return
        }

        val downloadFile = File(context.filesDir, s3Key.substringAfter("/"))
        if (downloadFile.exists()) downloadFile.delete()

        withContext(Dispatchers.IO) {
            val observer = transferUtility.download(bucket, s3Key, downloadFile)
            observer.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        runBlocking {
                            dataStore.edit { it[eTagKey] = newETag }

                            val jsonString = withContext(Dispatchers.IO) {
                                downloadFile.readText()
                            }
                            val jsonElement = Json.parseToJsonElement(jsonString)
                            val trendingKeywords: TrendingKeywords = Json.decodeFromJsonElement(jsonElement)
                            trendingKeywordsDeferred.complete(trendingKeywords)
                        }
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                override fun onError(id: Int, ex: Exception?) {
                    ex?.printStackTrace()
                }
            })
        }
    }

    suspend fun checkNotice(
        context: Context,
        s3Key: String,
        eTagKey: Preferences.Key<String>
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
        val s3Client = entryPoint.getS3Client()
        val transferUtility = entryPoint.getTransferUtility()
        val dataStore = entryPoint.getDataStore()
        val noticeDeferred = entryPoint.getNotice()

        val bucket = "sport-search-engine"
        val objectMetadata: ObjectMetadata = withContext(Dispatchers.IO) {
            s3Client.getObjectMetadata(bucket, s3Key)
        }
        val newETag = objectMetadata.eTag

        val currentETag = dataStore.data
            .map { preferences -> preferences[eTagKey] ?: "" }
            .first()

        if (newETag == currentETag) {
            val jsonString = withContext(Dispatchers.IO) {
                File(context.filesDir, s3Key.substringAfter("/")).readText()
            }
            val jsonElement = Json.parseToJsonElement(jsonString)
            val list: List<NoticeModel> = Json.decodeFromJsonElement(jsonElement)
            noticeDeferred.complete(list)

            return
        }

        val downloadFile = File(context.filesDir, s3Key.substringAfter("/"))
        if (downloadFile.exists()) downloadFile.delete()

        withContext(Dispatchers.IO) {
            val observer = transferUtility.download(bucket, s3Key, downloadFile)
            observer.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        runBlocking {
                            dataStore.edit { it[eTagKey] = newETag }

                            val jsonString = withContext(Dispatchers.IO) {
                                downloadFile.readText()
                            }
                            val jsonElement = Json.parseToJsonElement(jsonString)
                            val list: List<NoticeModel> = Json.decodeFromJsonElement(jsonElement)
                            noticeDeferred.complete(list)
                        }
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                override fun onError(id: Int, ex: Exception?) {
                    ex?.printStackTrace()
                }
            })
        }
    }

    suspend fun checkAutoCompleteJson(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
        val s3Client = entryPoint.getS3Client()
        val transferUtility = entryPoint.getTransferUtility()
        val dataStore = entryPoint.getDataStore()

        val bucket = "sport-search-engine"
        val key = "autocomplete/autocomplete.json"

        val objectMetadata: ObjectMetadata = withContext(Dispatchers.IO) {
            s3Client.getObjectMetadata(bucket, key)
        }
        val newETag = objectMetadata.eTag

        val currentETag = dataStore.data
            .map { preferences -> preferences[AUTOCOMPLETE_ETAG_KEY] ?: "" }
            .first()

        if (newETag == currentETag) {
            initTrie(context)
            return
        }

        val downloadFile = File(context.filesDir, "autocomplete.json")
        if (downloadFile.exists()) downloadFile.delete()

        withContext(Dispatchers.IO) {
            val observer = transferUtility.download(bucket, key, downloadFile)
            observer.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        runBlocking {
                            dataStore.edit { preferences ->
                                preferences[AUTOCOMPLETE_ETAG_KEY] = newETag
                            }

                            initTrie(context)
                        }
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                override fun onError(id: Int, ex: Exception?) {
                    ex?.printStackTrace()
                }
            })
        }
    }

    private suspend fun initTrie(
        context: Context
    ) {
        val trieDeferred = EntryPointAccessors.fromApplication(context, EntryPoint::class.java).getTrieDeferred()

        val file = File(context.filesDir, "autocomplete.json")
        if (file.exists()) {
            val jsonString = withContext(Dispatchers.IO) {
                file.readText()
            }
            val jsonElement = Json.parseToJsonElement(jsonString)

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }

            val autoCompleteData: List<KeywordInfo> = json.decodeFromJsonElement(jsonElement)

            val trie = Trie()
            for (autoComplete in autoCompleteData) {
                trie.insert(autoComplete.keyword)
                trie.insert(getChosung(autoComplete.keyword), autoComplete.keyword, autoComplete.weight!!)
            }

            trieDeferred.complete(Pair(trie, autoCompleteData))
        } else {
            trieDeferred.complete(Pair(Trie(), emptyList()))
        }
    }

    suspend fun checkNameDictionary(
        context: Context,
        category: String,
        s3Key: String,
        eTagKey: Preferences.Key<String>
    ) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
            val s3Client = entryPoint.getS3Client()
            val transferUtility = entryPoint.getTransferUtility()
            val dataStore = entryPoint.getDataStore()
            val translatedNameProvider = entryPoint.getTranslatedNameProvider()

            val bucket = "sport-search-engine"
            val objectMetadata: ObjectMetadata = withContext(Dispatchers.IO) {
                s3Client.getObjectMetadata(bucket, s3Key)
            }
            val newETag = objectMetadata.eTag

            val currentETag = dataStore.data
                .map { preferences -> preferences[eTagKey] ?: "" }
                .first()

            if (newETag == currentETag) {
                val jsonString = withContext(Dispatchers.IO) {
                    File(context.filesDir, s3Key.substringAfter("/")).readText()
                }
                val jsonElement = Json.parseToJsonElement(jsonString)
                val map: Map<String, String> = Json.decodeFromJsonElement(jsonElement)
                translatedNameProvider.setDictionary(category, map)

                return
            }

            val downloadFile = File(context.filesDir, s3Key.substringAfter("/"))
            if (downloadFile.exists()) downloadFile.delete()

            withContext(Dispatchers.IO) {
                val observer = transferUtility.download(bucket, s3Key, downloadFile)
                observer.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState?) {
                        if (state == TransferState.COMPLETED) {
                            runBlocking {
                                dataStore.edit { it[eTagKey] = newETag }

                                val jsonString = withContext(Dispatchers.IO) {
                                    downloadFile.readText()
                                }
                                val jsonElement = Json.parseToJsonElement(jsonString)
                                val map: Map<String, String> = Json.decodeFromJsonElement(jsonElement)
                                translatedNameProvider.setDictionary(category, map)
                            }
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                    override fun onError(id: Int, ex: Exception?) {
                        ex?.printStackTrace()
                    }
                })
            }
        } catch (e: Exception) {
            Log.d("awsS3", e.localizedMessage ?: "aws s3 error")
        }
    }

    suspend fun checkTournamentTeams(
        context: Context,
        category: String,
        s3Key: String,
        eTagKey: Preferences.Key<String>
    ) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
            val s3Client = entryPoint.getS3Client()
            val transferUtility = entryPoint.getTransferUtility()
            val dataStore = entryPoint.getDataStore()
            val tournamentTeamsDeferred = entryPoint.getTournamentTeamsDeferred()

            val bucket = "sport-search-engine"
            val objectMetadata: ObjectMetadata = withContext(Dispatchers.IO) {
                s3Client.getObjectMetadata(bucket, s3Key)
            }
            val newETag = objectMetadata.eTag

            val currentETag = dataStore.data
                .map { preferences -> preferences[eTagKey] ?: "" }
                .first()

            if (newETag == currentETag) {
                val jsonString = withContext(Dispatchers.IO) {
                    File(context.filesDir, s3Key.substringAfter("/")).readText()
                }
                val jsonElement = Json.parseToJsonElement(jsonString)
                val map: Map<String, List<Int?>> = Json.decodeFromJsonElement(jsonElement)
                tournamentTeamsDeferred.complete(map)

                return
            }

            val downloadFile = File(context.filesDir, s3Key.substringAfter("/"))
            if (downloadFile.exists()) downloadFile.delete()

            withContext(Dispatchers.IO) {
                val observer = transferUtility.download(bucket, s3Key, downloadFile)
                observer.setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState?) {
                        if (state == TransferState.COMPLETED) {
                            runBlocking {
                                dataStore.edit { it[eTagKey] = newETag }

                                val jsonString = withContext(Dispatchers.IO) {
                                    downloadFile.readText()
                                }
                                val jsonElement = Json.parseToJsonElement(jsonString)
                                val map: Map<String, List<Int?>> = Json.decodeFromJsonElement(jsonElement)
                                tournamentTeamsDeferred.complete(map)
                            }
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                    override fun onError(id: Int, ex: Exception?) {
                        ex?.printStackTrace()
                    }
                })
            }
        } catch (e: Exception) {
            Log.d("awsS3_team", e.localizedMessage ?: "aws s3 error")
        }
    }
}










