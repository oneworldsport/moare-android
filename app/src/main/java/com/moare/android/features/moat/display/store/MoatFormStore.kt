package com.moare.android.features.moat.display.store

import com.moare.android.core.store.BaseStore
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatUpdateRequest
import com.moare.android.features.moat.networking.MoatClient
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

sealed interface MoatFormAction {
    data class CreateMoat(val content: String) : MoatFormAction
    data class UpdateMoat(val moatId: String) : MoatFormAction
    data class DeleteMoat(val moatId: String) : MoatFormAction
}

class MoatFormStore @AssistedInject constructor(
    private val moatClient: MoatClient
) : BaseStore<MoatFormAction>() {

    @AssistedFactory
    interface Factory {
        fun create(
        ) : MoatFormStore
    }

    override fun send(action: MoatFormAction) {
        when (action) {
            is MoatFormAction.CreateMoat -> createMoat(action.content)
            is MoatFormAction.UpdateMoat -> updateMoat(action.moatId)
            is MoatFormAction.DeleteMoat -> deleteMoat(action.moatId)
        }
    }

    private fun createMoat(content: String) {
        val moat = MoatCreateRequest(content = content, sportTags = listOf("#축구"))

        scope.launch {
            try {
                moatClient.createMoat(moat)
            } catch (e: Exception) {

            }
        }
    }

    // 나중에
    private fun updateMoat(moatId: String) {
        val moat: MoatUpdateRequest? = null

        scope.launch {
            try {
                if (moat != null) {
                    moatClient.updateMoat(moatId = moatId, moat)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun deleteMoat(moatId: String) {
        scope.launch {
            try {
                moatClient.deleteMoat(moatId)
            } catch (e: Exception) {

            }
        }
    }
}