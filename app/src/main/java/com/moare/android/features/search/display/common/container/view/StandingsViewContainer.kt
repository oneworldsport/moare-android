package com.moare.android.features.search.display.common.container.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moare.android.features.search.display.football.view.FBPlayerStandingsDataList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsFirstCategoryItem
import com.moare.android.features.search.display.football.view.FBPlayerStandingsFirstCategoryList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsFirstDataList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsSecondCategoryList
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.util.CenterColumn

@Composable
fun StandingsViewContainer(
    displayDataState: ApiFetchState? = null,
    titleContent: @Composable ColumnScope.() -> Unit,
    firstCategoryContent: @Composable RowScope.() -> Unit,
    categoryListContent: @Composable RowScope.() -> Unit,
    standingsFirstDataContent: @Composable RowScope.() -> Unit,
    standingsDataContent: @Composable RowScope.() -> Unit,
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    CenterColumn(
        // NOTE: If set fillMaxSize, AnimatedVisibility works fine on first show.
        // But if fillMaxSize not set, AnimatedVisibility doesn't work on first show.
        // Not sure why yet
        modifier = Modifier.fillMaxSize()
    ) {
        this.titleContent()

        // category
        Row(
            modifier = Modifier.padding(top = 6.dp)
        ) {
            this.firstCategoryContent()

            Row(
                Modifier.horizontalScroll(horizontalScrollState)
            ) {
                this.categoryListContent()
            }
        }

        // loading
        AnimatedVisibility(
            visible = displayDataState == ApiFetchState.Fetching,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ProgressIndicator()
            }
        }

        // standings data
        AnimatedVisibility(
            visible = if (displayDataState == null) {
                true
            } else {
                displayDataState == ApiFetchState.Success
            }
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
            ) {
                Row {
                    this.standingsFirstDataContent()

                    Row(
                        Modifier.horizontalScroll(horizontalScrollState)
                    ) {
                        this.standingsDataContent()
                    }
                }
            }
        }
    }
}