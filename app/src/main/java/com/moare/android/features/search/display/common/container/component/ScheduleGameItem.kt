package com.moare.android.features.search.display.common.container.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.features.search.models.models.tennis.TennisGameInfoForSchedule
import com.moare.android.ui.common.components.GameStatusCapsuleButton
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun <T> ScheduleGameItem(
    state: ScheduleGameItemState<T>,
    actions: ScheduleGameItemActions
) {
    val game = state.game
    val leagueId = state.leagueId
    val teamNameDic = state.teamNameDic
    val homeTeamId = Constants.Ids.checkTeamId(leagueId, game.homeTeamId)
    val awayTeamId = Constants.Ids.checkTeamId(leagueId, game.awayTeamId)
    val homeTeamScore = game.homeTeamScore
    val awayTeamScore = game.awayTeamScore
    val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
    val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }

    val defaultHomeTeamName = if (leagueId in Constants.Ids.TENNIS_ALL) {
        game.gameInfo?.let { (it as? TennisGameInfoForSchedule)?.homeTeam?.name } ?: ""
    } else {
        ""
    }
    val defaultAwayTeamName = if (leagueId in Constants.Ids.TENNIS_ALL) {
        game.gameInfo?.let { (it as? TennisGameInfoForSchedule)?.awayTeam?.name } ?: ""
    } else {
        ""
    }

    val scoreAlpha by animateFloatAsState(
        targetValue = if (state.isResultOpened) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = state.isClickEnabled) {
                actions.onGameItemClick()
            }
            .padding(vertical = 8.dp)
            .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
    ) {

        /* ---------------------
           home
           --------------------- */
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
//                .clickable(enabled = fbGameStatsData != null) {
//                    searchViewModel.send(SearchViewModel.Intent.UpdateTextField(newValue = TextFieldValue(text = "토트넘")))
//                    searchViewModel.send(SearchViewModel.Intent.PerformSearch())
//                }
        ) {
            URLImage(
                url = Util.teamLogoUrl(leagueId, homeTeamId),
                size = URLImageSize.SMALL
            )

            // TODO: 그냥 id가 오류로 없는 경우도 "미정"이라고 나올 수 있음
            Text(
                text = if (homeTeamId == null) "미정" else teamNameDic["short_${homeTeamId}"] ?: defaultHomeTeamName,
                fontSize = 13.sp,
                maxLines = 2
            )

            if (state.shouldShowHomeLabel) {
                RoundedBorderText(
                    text = "홈",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare
                )
            }
        }

        // score
        CenterRow(
            modifier = Modifier.weight(0.8f).alpha(scoreAlpha)
        ) {
            CenterColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 축구 패널티킥 경기는 일반 스코어 검정색
                val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Color.Black
                } else {
                    if (homeTeamScore >= awayTeamScore) Moare else Color.Black
                }

                Text(
                    text = homeTeamScore.toString(),
                    textAlign = TextAlign.Center,
                    color = scoreColor
                )

                homeTeamPenaltyScore?.let {
                    awayTeamPenaltyScore?.let {
                        Text(
                            text = homeTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = if (homeTeamPenaltyScore >= awayTeamPenaltyScore) Moare else Color.Black
                        )
                    }
                }
            }

            if (state.shouldShowWinner && state.isHomeWinner) {
                RoundedBorderText(
                    text = "승",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        /* ---------------------
           game info
           --------------------- */
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // game status
            GameStatusCapsuleButton(
                gameStatusContext = state.gameStatusContext,
                leagueId = leagueId,
                isDisabled = state.isCapsuleButtonDisabled
            ) {
                actions.onCapsuleButtonClick()
            }

            // game date
            if (state.shouldShowOnlyDateTime) {
                Text(
                    text = CalendarUtil.formatDate(date = state.game.date, outputFormatType = OutputTimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            } else {
                Text(
                    text = CalendarUtil.formatDate(date = state.game.date).split(" ").firstOrNull() ?: "",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = CalendarUtil.formatDate(date = state.game.date, outputFormatType = OutputTimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // game type
            if (!state.gameType.isNullOrEmpty() &&state.shouldShowGameType) {
                Text(
                    text = state.gameType,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                )
            }

            // referee
            if (state.referee != null && state.shouldShowReferee) {
                Text(
                    text = "심판: ${state.referee}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                )
            }
        }

        /* ---------------------
           away
           --------------------- */
        // score
        CenterRow(
            modifier = Modifier.weight(0.8f).alpha(scoreAlpha)
        ) {
            if (state.shouldShowWinner && !state.isHomeWinner) {
                RoundedBorderText(
                    text = "승",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            CenterColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 축구 패널티킥 경기는 일반 스코어 검정색
                val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Color.Black
                } else {
                    if (awayTeamScore >= homeTeamScore) Moare else Color.Black
                }

                Text(
                    text = awayTeamScore.toString(),
                    textAlign = TextAlign.Center,
                    color = scoreColor
                )

                homeTeamPenaltyScore?.let {
                    awayTeamPenaltyScore?.let {
                        Text(
                            text = awayTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = if (awayTeamPenaltyScore >= homeTeamPenaltyScore) Moare else Color.Black
                        )
                    }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            URLImage(
                url = Util.teamLogoUrl(leagueId, awayTeamId),
                size = URLImageSize.SMALL
            )

            Text(
                text = if (awayTeamId == null) "미정" else teamNameDic["short_${awayTeamId}"] ?: defaultAwayTeamName,
                fontSize = 13.sp,
                maxLines = 2
            )

            if (state.shouldShowAwayLabel) {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray
                )
            }
        }
    }
}