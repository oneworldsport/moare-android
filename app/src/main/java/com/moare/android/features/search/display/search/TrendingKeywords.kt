package com.moare.android.features.search.display.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.components.HCapsuleBar
import com.moare.android.ui.util.clickableCapsuleRippleEffect

@Composable
fun TrendingKeywords(
    searchStore: SearchStore,
    onItemSelected: (String) -> Unit
) {
    val trendingKeywordList by searchStore.trendingKeywordList.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .height(40.dp)
            .padding(horizontal = 16.dp)
    ) {
        for ((index, value) in trendingKeywordList.withIndex()) {
            if (index != 0) {
                KeywordBox(value) {
                    onItemSelected(value)
                }
            }
        }
    }
}

@Composable
fun KeywordBox(
    keyword: String,
    onItemSelected: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickableCapsuleRippleEffect(onClick = onItemSelected)
    ) {
        Text(
            text = keyword,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        HCapsuleBar(color = Color.Gray)
    }
}