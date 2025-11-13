package com.moare.android.features.moat.display.moat.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.features.moat.display.components.CommentComposer
import com.moare.android.features.moat.display.components.MoatItem
import com.moare.android.features.moat.display.components.MoatType
import com.moare.android.features.moat.display.components.SettingWindow
import com.moare.android.features.moat.display.components.TextFieldAlert
import com.moare.android.features.moat.display.form.view.FormView
import com.moare.android.features.moat.display.moat.viewmodel.MoatIntent
import com.moare.android.features.moat.display.moat.viewmodel.MoatViewModel
import com.moare.android.features.moat.display.moat.viewmodel.MoatViewType
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.sign.display.signin.view.SignView
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.theme.Moare

@Composable
fun MoatTrendingView(
    moatViewModel: MoatViewModel = hiltViewModel()
) {
    val accessToken by moatViewModel.accessToken.collectAsState(initial = null)

    /*
    * viewmodel state
    * */

    val currentViewType by moatViewModel.currentViewType.collectAsState()
    val viewStack by moatViewModel.viewStack.collectAsState()
    val poppedView by moatViewModel.poppedView.collectAsState()
    val moatListResponse by moatViewModel.moatListResponse.collectAsState()
    val originalTrendingMoats by moatViewModel.originalTrendingMoats.collectAsState()
    val trendingMoats by moatViewModel.trendingMoats.collectAsState()
    val selectedMoat by moatViewModel.selectedMoat.collectAsState()
    val fireMap by moatViewModel.fireMap.collectAsState()
    val fireCountMap by moatViewModel.fireCountMap.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var settingsShowing by rememberSaveable { mutableStateOf(false) }
    var reportShowing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(accessToken) {
        if (!accessToken.isNullOrEmpty()) {
            moatViewModel.send(MoatIntent.GetTrendingMoats)
        }
    }

    BackHandler {
        moatViewModel.send(MoatIntent.Goback)
    }

    /*
       * ui
       * */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        /*
        * back button
        * */
        Column(
            Modifier.zIndex(1f)
        ) {
            Row {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.size(width = 34.dp, height = 50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                        contentDescription = null,
                        tint = Moare,
                        modifier = Modifier
                            .clickable {
                                moatViewModel.send(MoatIntent.Goback)
                            }
                    )
                }

                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))
        }

        if (!accessToken.isNullOrEmpty()) {
            if (currentViewType == MoatViewType.FORM) {
                FormView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 50.dp)
                ) {
                    items(trendingMoats.size) { index ->
                        val moat = trendingMoats[index]
                        val lines = moat.content.split('\n')
                        val title = lines.firstOrNull().orEmpty()
                        val body  = lines.drop(1).joinToString("\n")
                        val fired = fireMap[moat.moatId] ?: false
                        val fireCount = fireCountMap[moat.moatId] ?: moat.fireCount

                        LaunchedEffect(moat.moatId) {
                            moatViewModel.send(MoatIntent.CheckFire(moat.moatId))
                        }

                        MoatItem(
                            moatType = if (selectedMoat != null) MoatType.DETAIL else MoatType.TRENDING,
                            isButtonDisabled = selectedMoat != null,
                            title = title,
                            content = body,
                            hashtagList = moat.sportTags,
                            fireCount = fireCount,
                            commentCount = moat.commentCount,
                            userHandle = moat.userHandle,
                            createdAt = moat.createdAt,
                            settingTapped = {
                                settingsShowing = true
                            },
                            fired = fired,
                            fireTapped = { newValue ->
                                moatViewModel.send(MoatIntent.ToggleFire(moat.moatId, targetType = TargetType.MOAT))
                            },
                            action = {
                                moatViewModel.send(MoatIntent.SelectMoat(moatId = moat.moatId))
                            }
                        )
                    }

                    if (selectedMoat != null) {
                        val comments: List<MoatResponse> = selectedMoat?.commentListResponse?.moats.orEmpty()

                        item { HDivider(Modifier.padding(vertical = 8.dp)) }

                        items(comments.size) { index ->
                            val moat = comments[index]
                            val fired = fireMap[moat.moatId] ?: false
                            val fireCount = fireCountMap[moat.moatId] ?: moat.fireCount

                            LaunchedEffect(moat.moatId) {
                                moatViewModel.send(MoatIntent.CheckFire(moat.moatId))
                            }

                            MoatItem(
                                moatType = MoatType.COMMENT,
                                content = moat.content,
                                hashtagList = moat.sportTags,
                                fireCount = fireCount,
                                commentCount = moat.commentCount,
                                userHandle = moat.userHandle,
                                createdAt = moat.createdAt,
                                settingTapped = {},
                                fired = fired,
                                fireTapped = { newValue ->
                                    moatViewModel.send(MoatIntent.ToggleFire(moat.moatId, targetType = TargetType.COMMENT))
                                },
                                action = {
                                    moatViewModel.send(
                                        MoatIntent.SelectMoat(isComment = true, moatId = moat.moatId)
                                    )
                                }
                            )
                        }
                    }
                }

                if (currentViewType == MoatViewType.TRENDING) {
                    FloatingActionButton(
                        onClick = { moatViewModel.send(MoatIntent.AddViewStack(moatViewType = MoatViewType.FORM)) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        backgroundColor = Color.White,
                        contentColor = Moare,
//                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp) // 그림자 제거
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(40.dp))
                    }
                }

                if (currentViewType == MoatViewType.DETAIL) {
                    Row(
                        modifier = Modifier.align(Alignment.BottomStart).padding(horizontal = 8.dp)
                    ) {
                        CommentComposer(
                            text= text,
                            onTextChange = { newValue ->
                                text = newValue
                            },
                            action = {
                                moatViewModel.send(MoatIntent.CreateMoat(content = text))
                            },
                            modifier = Modifier
                        )
                    }
                }

                if (settingsShowing) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 50.dp, end = 25.dp)
                    ) {
                        SettingWindow(
                            reportTapped = {
                                settingsShowing = false
                                reportShowing = true
                            }
                        )
                    }
                }

                if (reportShowing) {
                    TextFieldAlert(
                        isPresent = reportShowing,
                        onDismiss = {
                            reportShowing = false
                        }
                    )
                }
            }
        } else {
            // 로그인 안 되어있을 때
            SignView()
        }
    }
}