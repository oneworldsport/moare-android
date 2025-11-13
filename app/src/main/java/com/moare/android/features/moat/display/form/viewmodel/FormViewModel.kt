package com.moare.android.features.moat.display.form.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatUpdateRequest
import com.moare.android.features.moat.networking.MoatClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FormIntent {
    data class CreateMoat(val content: String) : FormIntent()
    data class UpdateMoat(val moatId: String) : FormIntent()
    data class DeleteMoat(val moatId: String) : FormIntent()
}

@HiltViewModel
class FormViewModel @Inject constructor(
    private val moatClient: MoatClient
) : ViewModel() {

    fun send(intent: FormIntent) {
        viewModelScope.launch {
            when (intent) {
                is FormIntent.CreateMoat -> createMoat(intent.content)
                is FormIntent.UpdateMoat -> updateMoat(intent.moatId)
                is FormIntent.DeleteMoat -> deleteMoat(intent.moatId)
            }
        }
    }

    private suspend fun createMoat(content: String) {
        val moat = MoatCreateRequest(content = content, sportTags = listOf("#축구"))

        try {
            moatClient.createMoat(moat)
        } catch (e: Exception) {

        }
    }

    // 나중에
    private suspend fun updateMoat(moatId: String) {
        val moat: MoatUpdateRequest? = null

        try {
            if (moat != null) {
                moatClient.updateMoat(moatId = moatId, moat)
            }
        } catch (e: Exception) {

        }
    }

    private suspend fun deleteMoat(moatId: String) {
        try {
            moatClient.deleteMoat(moatId)
        } catch (e: Exception) {

        }
    }
}