package com.moare.android.features.userprofile.display.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.moat.display.components.MoatItem
import com.moare.android.features.moat.display.components.MoatType
import com.moare.android.ui.components.TextFieldAlert
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.userprofile.display.store.UserProfileAction
import com.moare.android.features.userprofile.display.store.UserProfileStore
import com.moare.android.ui.components.AppMenu
import com.moare.android.ui.components.HDivider
import com.moare.android.ui.components.ProfileImage
import com.moare.android.ui.components.ProfileImageSize
import com.moare.android.ui.util.CenterBox

@Composable
fun UserProfileView(
    store: UserProfileStore = hiltViewModel(),
    updateUserHandle: (String) -> Unit
) {
    val userProfile by store.userProfile.collectAsState()
    val userMoats by store.userMoats.collectAsState()
    val selectedMoat by store.selectedMoat.collectAsState()
    val fireMap by store.fireMap.collectAsState()
    val fireCountMap by store.fireCountMap.collectAsState()

    var reportShowing by rememberSaveable { mutableStateOf(false) }

    val profileImageUrl = userProfile?.profileImageUrl?.let { "https://moare-sns-profile-images.s3.ap-northeast-2.amazonaws.com/${it}" }

    LaunchedEffect(Unit) {
        store.send(UserProfileAction.GetUserProfile)
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            updateUserHandle(it.userHandle)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (selectedMoat == null) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .padding(horizontal = 8.dp)
            ) {
                ProfileImage(url = profileImageUrl, size = ProfileImageSize.BIG)

                userProfile?.bio?.let {
                    CenterBox(
                        height = 80.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = it,
                            maxLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        )
                    }
                }

                AppMenu(
                    label = {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = null
                        )
                    },
                    items = listOf("프로필 수정")
                ) { index, label ->
                    when (label) {
                        "프로필 수정" -> {
                            store.send(UserProfileAction.ShowUserProfileUpdateForm)
                        }
                    }
                }
            }


            userProfile?.sportsInterests?.let { sports ->
                if (sports.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        sports.forEachIndexed { index, sport ->
                            Text(
                                text = sport
                            )

                            if (index != sports.size - 1) {
                                Box(
                                    Modifier.size(width = 2.dp, height = 15.dp).background(Color.Black)
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
                    },
                    fired = fired,
                    fireTapped = { newValue ->
                        store.send(UserProfileAction.ToggleFire(userMoats[index].moatId, targetType = TargetType.MOAT))
                    },
                    action = {
                        store.send(UserProfileAction.SelectMoat(moatId = userMoats[index].moatId))
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
                            store.send(UserProfileAction.ToggleFire(userMoats[index].moatId, targetType = TargetType.COMMENT))
                        },
                        action = {
                            store.send(UserProfileAction.SelectMoat(true, moatId = comments[index].moatId))

                        }
                    )
                }
            }
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






























