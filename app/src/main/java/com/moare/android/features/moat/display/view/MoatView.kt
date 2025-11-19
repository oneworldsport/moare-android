package com.moare.android.features.moat.display.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.moare.android.features.moat.display.components.CommentComposer
import com.moare.android.features.moat.display.components.MoatItem
import com.moare.android.features.moat.display.components.MoatType
import com.moare.android.features.moat.display.components.SettingWindow
import com.moare.android.features.moat.display.components.TextFieldAlert
import com.moare.android.features.moat.display.store.MoatAction
import com.moare.android.features.moat.display.store.MoatStore
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.ui.components.HDivider
import com.moare.android.ui.theme.Moare

@Composable
fun MoatView(
    store: MoatStore
) {
    val accessToken by store.accessToken.collectAsState(initial = null)

    val moatListResponse by store.moatListResponse.collectAsState()
    val originalTrendingMoats by store.originalTrendingMoats.collectAsState()
    val trendingMoats by store.trendingMoats.collectAsState()
    val selectedMoat by store.selectedMoat.collectAsState()
    val fireMap by store.fireMap.collectAsState()
    val fireCountMap by store.fireCountMap.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var settingsShowing by rememberSaveable { mutableStateOf(false) }
    var reportShowing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (store.moatId != null) {
            // TODO: 뒤로가기를 통해 detail로 왔을때도 불필요하게 실행됨. 불필요한게 아닐수도 있지만 그래도 고민은 해봐야 할 것 같음.
            store.send(MoatAction.GetMoatDetail(moatId = store.moatId))
        } else {
            if (originalTrendingMoats.isEmpty()) {
                store.send(MoatAction.GetTrendingMoats)
            }
        }
    }

    BackHandler {
//        moatStore.send(MoatAction.Goback)
    }

    /*
       * ui
       * */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(trendingMoats.size) { index ->
                val moat = trendingMoats[index]
                val lines = moat.content.split('\n')
                val title = lines.firstOrNull().orEmpty()
                val body  = lines.drop(1).joinToString("\n")
                val fired = fireMap[moat.moatId] ?: false
                val fireCount = fireCountMap[moat.moatId] ?: moat.fireCount

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
                        store.send(MoatAction.ToggleFire(moat.moatId, targetType = TargetType.MOAT))
                    },
                    action = {
                        store.send(MoatAction.SelectMoat(moatId = moat.moatId))
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
                            store.send(MoatAction.ToggleFire(moat.moatId, targetType = TargetType.COMMENT))
                        },
                        action = {
                            store.send(
                                MoatAction.SelectMoat(moatId = moat.moatId)
                            )
                        }
                    )
                }
            }
        }

        if (store.isDetail) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                CommentComposer(
                    text= text,
                    onTextChange = { newValue ->
                        text = newValue
                    },
                    action = {
                        store.send(MoatAction.CreateMoat(content = text))
                    },
                    modifier = Modifier
                )
            }
        } else {
            FloatingActionButton(
                onClick = {
                    store.send(MoatAction.ShowMoatForm)
                },
                modifier = Modifier
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
}