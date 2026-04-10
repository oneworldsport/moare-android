package com.moare.android.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.models.mlb.MLBGameLineScore
import com.moare.android.ui.theme.MoareAndroidTheme

@Composable
fun CapsuleButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = color,
        textAlign = TextAlign.Center,
        modifier = if (isDisabled) {
            modifier
                .border(BorderStroke(1.dp, color), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        } else {
            modifier
                .clickable {
                    onClick()
                }
                .border(BorderStroke(1.dp, color), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        }
    )
}

sealed class GameStatusContext {
    data class Tennis(
        val status: Int?,
        val isResultOpened: Boolean = true
    ) : GameStatusContext()

    data class Nba(
        val status: Int,
        val period: Int? = null,
        val isResultOpened: Boolean = true
    ) : GameStatusContext()

    data class Mlb(
        val status: String,
        val currentInning: String? = null,
        val linescore: MLBGameLineScore? = null,
        val isResultOpened: Boolean = true
    ) : GameStatusContext()

    data class Football(
        val status: String,
        val elapsed: Int?,
        val extra: Int?,
        val isResultOpened: Boolean = true
    ) : GameStatusContext()

    data class Kbo(
        val status: String,
        val currentInning: String? = null,
        val isResultOpened: Boolean = true
    ) : GameStatusContext()
}

@Composable
fun GameStatusCapsuleButton(
    gameStatusContext: GameStatusContext,
    leagueId: Int,
    isDisabled: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val text: String = when (val context = gameStatusContext) {
        is GameStatusContext.Tennis -> {
            Constants.GameStatus.tennisGameStatusText(context.status)
        }
        is GameStatusContext.Nba -> {
            Constants.GameStatus.nbaGameStatusText(
                context.status,
                context.period
            )
        }
        is GameStatusContext.Mlb -> {
            Constants.GameStatus.mlbGameStatusText(
                status = context.status,
                currentInning = context.currentInning,
                linescore = context.linescore
            )
        }
        is GameStatusContext.Football -> {
            Constants.GameStatus.fbGameStatusText(
                status = context.status,
                elapsed = context.elapsed,
                extra = context.extra
            )
        }
        is GameStatusContext.Kbo -> {
            Constants.GameStatus.kboGameStatusText(
                status = context.status,
                currentInning = context.currentInning
            )
        }
    }

    val color: Color = when (gameStatusContext) {
        is GameStatusContext.Tennis -> {
            Constants.GameStatus.gameStatusColor(
                leagueId = leagueId,
                status = (gameStatusContext.status ?: 0).toString()
            )
        }
        is GameStatusContext.Nba -> {
            Constants.GameStatus.gameStatusColor(
                leagueId = leagueId,
                status = gameStatusContext.status.toString()
            )
        }
        is GameStatusContext.Mlb -> {
            Constants.GameStatus.gameStatusColor(
                leagueId = leagueId,
                status = gameStatusContext.status
            )
        }
        is GameStatusContext.Football -> {
            Constants.GameStatus.gameStatusColor(
                leagueId = leagueId,
                status = gameStatusContext.status
            )
        }
        is GameStatusContext.Kbo -> {
            Constants.GameStatus.gameStatusColor(
                leagueId = leagueId,
                status = gameStatusContext.status
            )
        }
    }

    CapsuleButton(
        text = text,
        color = color,
        modifier = modifier
    ) {
        onClick()
    }
}

@Preview(showBackground = true)
@Composable
fun CapsuleButtonPreview() {
    MoareAndroidTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CapsuleButton("경기 종료") {

            }
        }
    }
}