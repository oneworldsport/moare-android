package com.moare.android.features.sign.display.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moare.android.ui.components.HCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.clickableCapsuleRippleEffect

@Composable
fun SportList(
    selectedSports: List<String>,
    modifier: Modifier = Modifier,
    onItemSelect: (String) -> Unit
) {
    val sports = listOf("축구", "야구", "농구", "테니스", "F1", "배구", "골프")

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(sports) { item ->
            val isSelected = selectedSports.contains(item)

            CenterColumn(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .border(
                        BorderStroke(2.dp, if (isSelected) Moare else Color.Transparent),
                        RoundedCornerShape(20.dp)
                    )
                    .clickableCapsuleRippleEffect {
                        onItemSelect(item)
                    }
            ) {
                HCapsuleBar(
                    color = Color.Gray,
                    modifier = Modifier.alpha(if (isSelected) 0f else 0.8f)
                )

                Text(
                    text = item,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}