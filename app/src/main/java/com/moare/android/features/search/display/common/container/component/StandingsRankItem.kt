package com.moare.android.features.search.display.common.container.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.nullableOptionalClickable

@Composable
fun StandingsRankItem(
    id: Int = 0,
    width: Dp? = null,
    isGameStats: Boolean = false,
    rank: Int = 0,
    imageUrl: String?,
    isSvgLogo: Boolean = false,
    name: String,
    subName: String? = null,
    extraInfo: String? = null,
    extraSubInfo: String? = null,
    isLastItem: Boolean = false,
    action: (id: Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(width ?: 132.dp)
            .padding(start = 10.dp)
            .height(40.dp)
            .nullableOptionalClickable(apply = id != 0) {
                action(id)
            }
    ) {
        if (!isGameStats) {
            Text(
                text = "$rank",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                modifier = Modifier
                    .width(if (rank >= 100) 30.dp else 22.dp)
            )
        }

        URLImage(
            url = imageUrl,
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp),
            isSvg = isSvgLogo
        )

        if (isGameStats) {
            Text(
                text = name,
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
//                    .width(60.dp)
            )

            // TODO: goals, cards, number, captain
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(30.dp)
                    .padding(start = 2.dp)
            ) {
                Text(
                    text = extraInfo ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
//                        .alpha(if (data.position.isNotBlank()) 1f else 0.7f)
                )

                Text(
                    text = extraSubInfo ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .alpha(0.7f)
                )
            }
        } else {
            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                subName?.let {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        VCapsuleBar(
            modifier = Modifier.alpha(0.5f),
            topRound = false,
            bottomRound = isLastItem
        )
    }
}