package com.moare.android.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.util.DayInfo
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import java.time.DayOfWeek

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
            modifier = Modifier.padding(horizontal = 4.dp),
            isSvg = url.contains(".svg")
        )

        Text(
//            text = leagueName + " " + leagueSeason.toString().takeLast(2) + "/25",
            text = "${leagueName} ${leagueSeason}-${(leagueSeason + 1).toString().takeLast(2)}",
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NBATitle(
    leagueName: String,
    leagueSeason: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.nba_logo),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 4.dp).size(30.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "${leagueName} ${leagueSeason}-${(leagueSeason + 1).toString().takeLast(2)}",
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RoundedBorderText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 17.sp,
    borderWidth: Dp = 1.dp,
    radius: Dp = 2.dp,
    textColor: Color = Color.Black,
    borderColor: Color = Color.Black
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = textColor,
        modifier = modifier
                .border(BorderStroke(borderWidth, borderColor), RoundedCornerShape(radius))
                .padding(horizontal = 6.dp, vertical = 3.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun TextComponentPreview() {
    MoareAndroidTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            RoundedBorderText(
                text = "홈",
                fontSize = 11.sp,
                radius = 4.dp,
                textColor = Moare,
                borderColor = Moare
            )
        }
    }
}