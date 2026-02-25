package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.graphics.Color
import com.moare.android.features.search.models.models.common.GameForSchedule

data class ScheduleGameItemState<T>(
    val leagueId: Int,
    val game: GameForSchedule<T>,
    val teamNameDic: Map<String, String>,
    val isClickEnabled: Boolean = true,
    val isResultOpened: Boolean = false,
    val gameStatusText: String,
    val gameStatusColor: Color,
    val isCapsuleButtonDisabled: Boolean = false,
    val gameType: String? = null,
    val referee: String? = null,
    val shouldShowOnlyDateTime: Boolean = true,
    val shouldShowGameType: Boolean = true,
    val shouldShowReferee: Boolean = false,
    val shouldShowHomeLabel: Boolean = false,
    val shouldShowAwayLabel: Boolean = false,

    val shouldShowWinner: Boolean = false,
    val isHomeWinner: Boolean = true
)

data class ScheduleGameItemActions(
    val onGameItemClick: () -> Unit,
    val onCapsuleButtonClick: () -> Unit
)