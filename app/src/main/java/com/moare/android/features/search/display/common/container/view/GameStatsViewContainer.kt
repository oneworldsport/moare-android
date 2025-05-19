package com.moare.android.features.search.display.common.container.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.UIConstants
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.ui.util.CenterColumn

@Composable
fun GameStatsViewContainer(
    state: GameStatsContainerState,
    titleContent: @Composable RowScope.() -> Unit,
    gameContent: @Composable ColumnScope.() -> Unit,
    teamButtonContent: @Composable ColumnScope.() -> Unit,
    coachContent: @Composable ColumnScope.() -> Unit = {},
    categoryListContent: @Composable RowScope.() -> Unit,
    standingsFirstDataContent: @Composable RowScope.() -> Unit,
    standingsDataContent: @Composable RowScope.() -> Unit,
) {
    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.shouldShowTitle) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                this.titleContent()
            }
        }

        if (state.shouldShowGameItem) {
            this.gameContent()
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
                .background(MaterialTheme.colors.primary)
        )

        if (state.shouldShowStats) {
            this.teamButtonContent()

            if (state.shouldShowCoach) {
                this.coachContent()
            }

            /* ---------------------
               players stats
               --------------------- */
            StandingsViewContainer(
                state = StandingsContainerState(
                    firstCategoryItemHeight = state.firstCategoryItemHeight
                ),
                headerContent = {},
                categoryListContent = {
                    this.categoryListContent()
                },
                standingsFirstDataContent = {
                    this.standingsFirstDataContent()
                },
                standingsDataContent = {
                    this.standingsDataContent()
                }
            )
        } else {
            Text(
                text = "경기 시작 후 데이터가 업데이트됩니다.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))
        }
    }
}