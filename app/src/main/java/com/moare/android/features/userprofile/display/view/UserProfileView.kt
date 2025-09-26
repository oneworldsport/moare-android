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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.features.moat.display.components.MoatItem
import com.moare.android.features.moat.display.components.MoatType
import com.moare.android.features.userprofile.display.viewmodel.UserProfileIntent
import com.moare.android.features.userprofile.display.viewmodel.UserProfileViewModel
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.theme.Moare

@Composable
fun UserProfileView(
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val userProfile by userProfileViewModel.userProfile.collectAsState()
    val userMoats by userProfileViewModel.userMoats.collectAsState()
    val selectedMoat by userProfileViewModel.selectedMoat.collectAsState()

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
                                text = userProfile?.nickname ?: ""
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

                    MoatItem(
                        moatType = if (selectedMoat != null) MoatType.DETAIL else MoatType.TIMELINE,
                        isButtonDisabled = selectedMoat != null,
                        title = title,
                        content = body,
                        hashtagList = userMoats[index].sportType,
                        fireCount = userMoats[index].fireCount,
                        commentCount = userMoats[index].commentCount,
                        nickname = userMoats[index].nickname,
                        createdAt = userMoats[index].createdAt,
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
                        MoatItem(
                            moatType = MoatType.COMMENT,
                            content = comments[index].content,
                            hashtagList = comments[index].sportType,
                            fireCount = comments[index].fireCount,
                            commentCount = comments[index].commentCount,
                            nickname = comments[index].nickname,
                            createdAt = comments[index].createdAt,
                            action = {
                                userProfileViewModel.send(UserProfileIntent.SelectMoat(true, moatId = comments[index].moatId))

                            }
                        )
                    }
                }
            }
        }
    }
}