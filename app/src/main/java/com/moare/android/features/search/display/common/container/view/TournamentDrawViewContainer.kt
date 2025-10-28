package com.moare.android.features.search.display.common.container.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.features.search.display.common.container.component.TournamentSingleGameItem
import com.moare.android.features.search.display.common.container.state.TournamentDrawContainerState
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.CenterColumn

@Composable
fun <T> TournamentDrawViewContainer(
    state: TournamentDrawContainerState<T>
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp)
    ) {
        state.gameListTuple.forEachIndexed { roundIndex, item ->
            val title = item.title
            val gameList = item.gameList

            CenterColumn {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(250.dp)
                )
                HCapsuleBar(
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                )

                gameList.forEachIndexed { index, games ->
                    if (state.isSeries) {
                        // TODO: 추첨인데 시리즈인 경우가 생기면 작업
                    } else {
                        games?.firstOrNull()?.let {
                            TournamentSingleGameItem(
                                leagueId = state.leagueId,
                                game = it,
                                teamNameDic = state.teamNameDic,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }

            if (roundIndex != state.gameListTuple.size - 1) {
                // TODO: Scroll 화면에서는 .fillMaxHeight() 안먹힘. 다른 방법 생각해 봐야함.
//                VCapsuleBar(
//                    modifier = Modifier
//                        .padding(top = 40.dp, bottom = 12.dp)
//                        .alpha(0.5f)
//                )
            }
        }
    }
}