package com.moare.android.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.ObjectMetadata
import com.moare.android.core.di.EntryPoint
import com.moare.android.features.search.models.AutoComplete
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.lang.Exception

object AWSUtils {
    private val ETAG_KEY = stringPreferencesKey("autoCompleteETag")

    suspend fun checkAutoCompleteJson(
        context: Context
    ) {
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
            .map { preferences -> preferences[ETAG_KEY] ?: "" }
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
                                preferences[ETAG_KEY] = newETag
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

            val autoCompleteData: List<AutoComplete> = json.decodeFromJsonElement(jsonElement)

            val trie = Trie()
            for (autoComplete in autoCompleteData) {
                trie.insert(autoComplete.word)
                trie.insert(getChosung(autoComplete.word), autoComplete.word, autoComplete.weight)
            }

            trieDeferred.complete(trie)
        } else {
            trieDeferred.complete(Trie())
        }
    }
}