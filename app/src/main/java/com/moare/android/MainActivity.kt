package com.moare.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.mvi.AppViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moare.android.features.moat.display.moat.view.MoatTimelineView
import com.moare.android.features.search.display.search.SearchView
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.userprofile.display.view.UserProfileView
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isSplashFinished by remember { mutableStateOf(false) }

//            val viewForTest: SportDisplayType = SportDisplayType.KBO_PLAYER_INFO
            val viewForTest: SportDisplayType? = null

            val navController = rememberNavController()
            val items = listOf(Screen.Search, Screen.Moat, Screen.Profile)

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
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Search : Screen("search", "검색", Icons.Rounded.Search)
    object Moat : Screen("moat", "모트", Icons.AutoMirrored.Outlined.Chat)
    object Profile : Screen("profile", "내 프로필", Icons.Rounded.PersonOutline)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MoareAndroidTheme {
    }
}