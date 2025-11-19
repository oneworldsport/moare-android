package com.moare.android.features.userprofile.display.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.features.moat.display.components.MoatItem
import com.moare.android.features.moat.display.components.MoatType
import com.moare.android.features.moat.display.components.SettingWindow
import com.moare.android.features.moat.display.components.TextFieldAlert
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.userprofile.display.store.UserProfileIntent
import com.moare.android.features.userprofile.display.store.UserProfileViewModel
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.theme.Moare

@Composable
fun UserProfileView(
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val userMoats by userProfileViewModel.userMoats.collectAsState()
    val selectedMoat by userProfileViewModel.selectedMoat.collectAsState()
    val fireMap by userProfileViewModel.fireMap.collectAsState()
    val fireCountMap by userProfileViewModel.fireCountMap.collectAsState()

    var settingsShowing by rememberSaveable { mutableStateOf(false) }
    var reportShowing by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userProfileViewModel.send(UserProfileIntent.GetUserProfile)
    }

    BackHandler {
        userProfileViewModel.send(UserProfileIntent.Goback)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
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
                                userProfileViewModel.send(UserProfileIntent.Goback)
                            }
                    )
                }

                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 50.dp).padding(horizontal = 8.dp)
        ) {
            if (selectedMoat == null) {
                Row {
                    Box(
                        Modifier.size(80.dp)
                            .background(
                                color = Moare,
                                shape = CircleShape)
                    )

                    Column {
                        Row {
                            Text(
                                text = userProfile?.userHandle ?: ""
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                Icons.Outlined.Settings,
                                ""
                            )

                            val sports = userProfile?.sportsInterests

                            if (!sports.isNullOrEmpty()) {
                                sports.forEach { sport ->
                                    Text(
                                        sport
                                    )
                                }
                            }
                        }
                    }
                }

                HDivider(
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(userMoats.size) { index ->
                    val lines = userMoats[index].content.split('\n')
                    val title = lines.firstOrNull() ?: ""
                    val body = lines.drop(1).joinToString("\n")
                    val fired = fireMap[userMoats[index].moatId] ?: false
                    val fireCount = fireCountMap[userMoats[index].moatId] ?: userMoats[index].fireCount

                    LaunchedEffect(userMoats[index].moatId) {
                        userProfileViewModel.send(UserProfileIntent.CheckFire(userMoats[index].moatId))
                    }

                    MoatItem(
                        moatType = if (selectedMoat != null) MoatType.DETAIL else MoatType.TRENDING,
                        isButtonDisabled = selectedMoat != null,
                        title = title,
                        content = body,
                        hashtagList = userMoats[index].sportTags,
                        fireCount = fireCount,
                        commentCount = userMoats[index].commentCount,
                        userHandle = userMoats[index].userHandle,
                        createdAt = userMoats[index].createdAt,
                        settingTapped = {
                            settingsShowing = true
                        },
                        fired = fired,
                        fireTapped = { newValue ->
                            userProfileViewModel.send(UserProfileIntent.ToggleFire(userMoats[index].moatId, targetType = TargetType.MOAT))
                        },
                        action = {
                            userProfileViewModel.send(UserProfileIntent.SelectMoat(moatId = userMoats[index].moatId))
                        }
                    )
                }
            }

            if (selectedMoat != null) {
                val comments = selectedMoat?.commentListResponse?.moats ?: listOf()

                LazyColumn {
                    items(comments.size) { index ->
                        val fired = fireMap[comments[index].moatId] ?: false
                        val fireCount = fireCountMap[comments[index].moatId] ?: comments[index].fireCount

                        LaunchedEffect(comments[index]) {
                            userProfileViewModel.send(UserProfileIntent.CheckFire(userMoats[index].moatId))
                        }

                        MoatItem(
                            moatType = MoatType.COMMENT,
                            content = comments[index].content,
                            hashtagList = comments[index].sportTags,
                            fireCount = fireCount,
                            commentCount = comments[index].commentCount,
                            userHandle = comments[index].userHandle,
                            createdAt = comments[index].createdAt,
                            settingTapped = {},
                            fired = fired,
                            fireTapped = { newValue ->
                                userProfileViewModel.send(UserProfileIntent.ToggleFire(userMoats[index].moatId, targetType = TargetType.COMMENT))
                            },
                            action = {
                                userProfileViewModel.send(UserProfileIntent.SelectMoat(true, moatId = comments[index].moatId))

                            }
                        )
                    }
                }
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