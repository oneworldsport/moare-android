package com.moare.android.features.userprofile.display.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.userprofile.display.components.SportsSelectForm
import com.moare.android.features.userprofile.display.store.UserProfileUpdateFormAction
import com.moare.android.features.userprofile.display.store.UserProfileUpdateFormStore
import com.moare.android.ui.components.CapsuleButton
import com.moare.android.ui.components.ProgressIndicator
import com.moare.android.ui.components.UpdateFormProfileImage
import com.moare.android.ui.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun UserProfileUpdateFormView(
    store: UserProfileUpdateFormStore
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    var labelWidth by remember { mutableStateOf(0.dp) }

    val tempImageUrl by store.tempImageUrl.collectAsState()
    val userHandleText by store.userHandleText.collectAsState()
    val bioText by store.bioText.collectAsState()
    val isUserHandleTextFieldEnabled by store.isUserHandleTextFieldEnabled.collectAsState()
    val userHandleCheckState by store.userHandleCheckState.collectAsState()
    val sportsInterests by store.sportsInterests.collectAsState()

    val userProfile = store.userProfile
    val profileImageUrl = userProfile.profileImageUrl?.let { "https://moare-sns-profile-images.s3.ap-northeast-2.amazonaws.com/${it}" }

    // image
//    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
//            selectedUri = uri
            store.send(UserProfileUpdateFormAction.ShowImageEdit(uri))
        }
    }

    CenterColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // 프로필 이미지
        UpdateFormProfileImage(
            url = tempImageUrl ?: profileImageUrl,
            size = 120.dp
        ) {
            pickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        // 사용자 이름
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            CenterRow {
                Text(
                    text = "사용자 이름",
                    fontSize = 15.sp,
                    modifier = Modifier.width(labelWidth)
                )

                VCapsuleBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Moare,
                    customHeight = 20.dp,
                    customWidth = 1.dp
                )
            }

            Column {
                CenterRow(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(20.dp)
                ) {
                    BasicTextField(
                        value = userHandleText,
                        onValueChange = { newValue ->
                            store.send(UserProfileUpdateFormAction.CheckUserHandle(newValue))
                        },
                        enabled = isUserHandleTextFieldEnabled,
                        modifier = Modifier.weight(1f)
                    )

                    AnimatedVisibility(
                        visible = userHandleCheckState == ApiFetchState.Fetching,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ProgressIndicator(size = 20.dp)
                    }

                    AnimatedVisibility(
                        visible = userHandleCheckState == ApiFetchState.Success,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Moare
                        )
                    }
                }

                AnimatedVisibility(
                    visible = userHandleCheckState is ApiFetchState.Error,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    (userHandleCheckState as? ApiFetchState.Error)?.message?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            color = Moare,
                        )
                    }
                }
            }
        }

        // 소개
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            CenterRow {
                Text(
                    text = "소개",
                    fontSize = 15.sp,
                    modifier = Modifier.width(labelWidth)
                )

                VCapsuleBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Moare,
                    customHeight = 20.dp,
                    customWidth = 1.dp
                )
            }

            BasicTextField(
                value = bioText,
                onValueChange = { newValue ->
                    store.send(UserProfileUpdateFormAction.UpdateBio(newValue))
                },
                modifier = Modifier.height(100.dp)
            )
        }

        // 관심 스포츠
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            CenterRow {
                Text(
                    text = "관심 스포츠",
                    fontSize = 15.sp,
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            with(density) {
                                labelWidth = layoutCoordinates.size.width.toDp()
                            }
                        }
                )

                VCapsuleBar(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Moare,
                    customHeight = 20.dp,
                    customWidth = 1.dp
                )
            }

            CenterRow(
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                for ((index, value) in sportsInterests.withIndex()) {
                    Text(
                        text = value
                    )

                    if (index != sportsInterests.size - 1) {
                        VCapsuleBar(
                            customWidth = 1.dp,
                            customHeight = 15.dp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        SportsSelectForm(sportsInterests) { sport ->
            store.send(UserProfileUpdateFormAction.UpdateSportsInterests(sport))
        }

        CapsuleButton(
            text = "완료",
            fontSize = 16,
            borderWidth = 2.dp
        ) {
            store.send(UserProfileUpdateFormAction.Submit(context))
        }
    }
}