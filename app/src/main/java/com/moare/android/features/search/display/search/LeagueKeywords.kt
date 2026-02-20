package com.moare.android.features.search.display.search

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.LeagueKeywords
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun LeagueKeywords(
    leagueKeywords: LeagueKeywords,
    modifier: Modifier,
    onItemSelected: (KeywordInfo) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "blink")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2400
                1f at 0
                1f at 500                 // 1f 유지(멈춤)
                0f at 1200 with LinearEasing // 서서히 꺼짐
                1f at 1900 with LinearEasing // 서서히 켜짐
                1f at 2400
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    CenterRow(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (leagueKeywords.live.isNotEmpty()) {
            CenterColumn(
                modifier = Modifier.padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HCapsuleBar()

                CenterRow {
                    Box(
                        modifier = Modifier
                            .alpha(alpha)
                            .padding(end = 10.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Moare)
                    )

                    Text(
                        text = "경기중"
                    )
                }

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 150.dp)
                ) {
                    items(leagueKeywords.live) { item ->
                        KeywordBox(item.keyword) {
                            onItemSelected(item)
                        }
                    }
                }
            }
        }

        if (leagueKeywords.recent.isNotEmpty()) {
            CenterColumn(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HCapsuleBar()

                Text(
                    text = "최근 결과"
                )

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 150.dp)
                ) {
                    items(leagueKeywords.recent) { item ->
                        KeywordBox(item.keyword) {
                            onItemSelected(item)
                        }
                    }
                }
            }
        }
    }
}




























