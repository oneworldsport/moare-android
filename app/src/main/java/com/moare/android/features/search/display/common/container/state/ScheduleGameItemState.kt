package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.graphics.Color

data class ScheduleGameItemState(
    val isClickEnabled: Boolean = true,
    val homeTeamLogo: String?,
    val homeTeamName: String,
    val homeTeamScore: Int,
    val awayTeamLogo: String?,
    val awayTeamName: String,
    val awayTeamScore: Int,
    val isResultOpened: Boolean = false,
    val gameStatusText: String,
    val gameStatusColor: Color,
    val isCapsuleButtonDisabled: Boolean = false,
    val date: String,
    val gameType: String? = null,
    val referee: String? = null,
    val shouldShowOnlyDateTime: Boolean = true,
    val shouldShowGameType: Boolean = true,
    val shouldShowReferee: Boolean = false,
    val shouldShowHomeLabel: Boolean = false,
    val shouldShowAwayLabel: Boolean = false,
    val isSvgLogo: Boolean = false
)

data class ScheduleGameItemActions(
    val onGameItemClick: () -> Unit,
    val onCapsuleButtonClick: () -> Unit
)