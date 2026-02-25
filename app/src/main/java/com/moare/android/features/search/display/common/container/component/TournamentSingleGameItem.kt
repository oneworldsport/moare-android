package com.moare.android.features.search.display.common.container.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.core.util.Util
import com.moare.android.features.search.models.models.common.GameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun <T> TournamentSingleGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    game: GameForSchedule<T>,
    modifier: Modifier = Modifier,
) {
    val homeTeamId = game.homeTeamId
    val awayTeamId = game.awayTeamId
    val homeTeamScore = game.homeTeamScore
    val awayTeamScore = game.awayTeamScore
    val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
    val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
    val elapsed = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.status?.elapsed }
    val gameStatusText = Constants.GameStatus.gameStatusText(leagueId, game.gameStatus, elapsed)
    val shouldShowScore = !Constants.GameStatus.isBeforeGame(leagueId, game.gameStatus)
    val isFinished = gameStatusText == StringConstants.GAME_FINISHED_STR

    val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
        homeTeamPenaltyScore > awayTeamPenaltyScore
    } else {
        homeTeamScore > awayTeamScore
    }

    CenterRow(modifier = modifier) {
        CenterColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .width(80.dp)
                .alpha(if (isFinished && !isHomeWinner) 0.3f else 1f)
        ) {
            if (isFinished && isHomeWinner) {
                HCapsuleBar(modifier = Modifier.padding(bottom = 4.dp))
            }

            URLImage(
                url = Util.teamLogoUrl(leagueId, homeTeamId),
                size = URLImageSize.SMALL
            )

            Text(
                text = teamNameDic["short_${homeTeamId}"] ?: "",
                fontSize = 13.sp,
                maxLines = 2
            )
        }

        if (shouldShowScore) {
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
                    color = scoreColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(30.dp)
                )

                if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Text(
                        text = homeTeamPenaltyScore.toString(),
                        fontSize = 12.sp,
                        color = if (homeTeamPenaltyScore >= awayTeamPenaltyScore) Moare else Color.Black
                    )
                }
            }
        }

        CenterColumn(
            modifier = Modifier.width(110.dp)
        ) {
            // game status
            CapsuleButton(
                text = gameStatusText,
                color = Constants.GameStatus.gameStatusColor(leagueId, game.gameStatus)
            ) { }

            // game date
            Text(
                text = CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: "",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = CalendarUtil.formatDate(game.date, outputFormatType = OutputTimeFormatType.AMPM),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        if (shouldShowScore) {
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
                    color = scoreColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(30.dp)
                )

                if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Text(
                        text = awayTeamPenaltyScore.toString(),
                        fontSize = 12.sp,
                        color = if (awayTeamPenaltyScore >= homeTeamPenaltyScore) Moare else Color.Black
                    )
                }
            }
        }

        CenterColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .width(80.dp)
                .alpha(if (isFinished && isHomeWinner) 0.3f else 1f)
        ) {
            if (isFinished && !isHomeWinner) {
                HCapsuleBar(modifier = Modifier.padding(bottom = 4.dp))
            }

            URLImage(
                url = Util.teamLogoUrl(leagueId, awayTeamId),
                size = URLImageSize.SMALL
            )

            Text(
                text = teamNameDic["short_${awayTeamId}"] ?: "",
                fontSize = 13.sp,
                maxLines = 2
            )
        }
    }
}


















