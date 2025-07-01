package com.moare.android.features.search.display.common.container.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
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
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare

@Composable
fun ScheduleGameItem(
    state: ScheduleGameItemState,
    actions: ScheduleGameItemActions
) {
    val homeTeamScore = state.homeTeamScore
    val awayTeamScore = state.awayTeamScore

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
                url = state.homeTeamLogo,
                size = URLImageSize.SMALL,
                isSvg = state.isSvgLogo
            )

            Text(
                text = state.homeTeamName,
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

        // Add space to both sides of each score to place the score in the middle
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = state.homeTeamScore.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(20.dp)
                .alpha(scoreAlpha),
            color = if (homeTeamScore >= awayTeamScore) MaterialTheme.colors.primary else Color.Black
        )

        // Add space to both sides of each score to place the score in the middle
        Spacer(Modifier.weight(0.3f))

        /* ---------------------
           game info
           --------------------- */
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // game status
            CapsuleButton(
                text = state.gameStatusText,
                color = state.gameStatusColor,
                isDisabled = state.isCapsuleButtonDisabled
            ) {
                actions.onCapsuleButtonClick()
            }

            // game date
            if (state.shouldShowOnlyDateTime) {
                Text(
                    text = CalendarUtil.formatDate(date = state.date, formatType = TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            } else {
                Text(
                    text = CalendarUtil.formatDate(date = state.date).split(" ").firstOrNull() ?: "",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = CalendarUtil.formatDate(date = state.date, formatType = TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // venue
            if (state.shouldShowVenue) {
                Text(
                    text = "장소: ${state.venue}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // game type
            if (state.gameType != null && state.shouldShowGameType) {
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
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = state.awayTeamScore.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(20.dp)
                .alpha(scoreAlpha),
            color = if (awayTeamScore >= homeTeamScore) MaterialTheme.colors.primary else Color.Black
        )

        Spacer(Modifier.weight(0.3f))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            URLImage(
                url = state.awayTeamLogo,
                size = URLImageSize.SMALL,
                isSvg = state.isSvgLogo
            )

            Text(
                text = state.awayTeamName,
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