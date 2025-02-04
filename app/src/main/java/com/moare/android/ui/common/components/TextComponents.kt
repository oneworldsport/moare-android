package com.moare.android.ui.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LeagueTitle(
    url: String,
    leagueName: String,
    leagueSeason: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        URLImage(
            url = url,
            size = URLImageSize.SMALL,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // TODO: make season text to use util
        Text(
            text = leagueName + " " + leagueSeason.toString().takeLast(2) + "/25",
            fontWeight = FontWeight.Medium
        )
    }
}