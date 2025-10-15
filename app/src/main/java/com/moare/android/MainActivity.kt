package com.moare.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.mvi.AppViewModel
import com.moare.android.features.search.display.search.SearchView
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.ui.theme.MoareAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoareAndroidTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot(viewModel: AppViewModel = hiltViewModel()) {
    var isSplashFinished by remember { mutableStateOf(false) }

//    val viewForTest: SportDisplayType? = SportDisplayType.KBO_GAME_STATS
    val viewForTest: SportDisplayType? = null

    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        if (viewForTest != null) {
            SearchView(
                viewModel = viewModel,
                searchStore = viewModel.searchStore,
                viewForTest = viewForTest
            )
        } else {
            SearchView(
                viewModel = viewModel,
                searchStore = viewModel.searchStore
            )

            if (!isSplashFinished) {
                SplashView {
                    isSplashFinished = true
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MoareAndroidTheme {
    }
}