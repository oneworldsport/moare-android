package com.moare.android.features.moat.display.timeline.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import com.moare.android.features.sign.models.AuthTokenData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MoatTimelineViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val idToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("idToken")]
        }
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("accessToken")]
        }
    val refreshToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("refreshToken")]
        }
}