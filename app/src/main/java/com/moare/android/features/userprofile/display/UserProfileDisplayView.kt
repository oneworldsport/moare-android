package com.moare.android.features.userprofile.display

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.features.moat.display.AccessTokenState
import com.moare.android.features.moat.display.MoatStackAction
import com.moare.android.features.moat.display.MoatStackItemView
import com.moare.android.features.moat.display.MoatViewType
import com.moare.android.features.moat.display.view.MoatView
import com.moare.android.features.sign.display.signin.view.SignView
import com.moare.android.features.userprofile.display.store.UserProfileAction
import com.moare.android.features.userprofile.display.view.UserProfileView
import com.moare.android.ui.components.BackButton

@Composable
fun UserProfileDisplayView(
    viewModel: UserProfileStackViewModel
) {
    val accessTokenState by viewModel.accessTokenState.collectAsState()
    val stack by viewModel.stack.collectAsState()
    val isBootstrapped by viewModel.isBootstrapped.collectAsState()

    var userHandle by remember { mutableStateOf("") }

    val currentViewType = when (stack.lastOrNull()) {
        is UserProfileStackItem.UserProfile -> UserProfileViewType.USER_PROFILE
        is UserProfileStackItem.MoatDetail -> UserProfileViewType.MOAT_DETAIL
        else -> UserProfileViewType.USER_PROFILE
    }

    LaunchedEffect(accessTokenState) {
        when (val state = accessTokenState) {
            AccessTokenState.Loading -> {
            }
            is AccessTokenState.Loaded -> {
                val token = state.token

                if (!isBootstrapped && !token.isNullOrBlank()) {
                    viewModel.send(UserProfileStackAction.BootstrapSession)
                    return@LaunchedEffect
                }

                if (token.isNullOrBlank()) {
                    viewModel.send(UserProfileStackAction.EmptyStack)
                } else {
                    if (stack.isEmpty()) {
                        viewModel.send(UserProfileStackAction.Push(UserProfileViewType.USER_PROFILE))
                    }
                }
            }
        }
    }

    BackHandler {
        viewModel.send(UserProfileStackAction.Pop)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                viewModel.send(UserProfileStackAction.Pop)
            }

            if (currentViewType != UserProfileViewType.PROFILE_UPDATE_FORM) {
                Text(
                    text = userHandle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (currentViewType != UserProfileViewType.PROFILE_UPDATE_FORM) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        when (val state = accessTokenState) {
            AccessTokenState.Loading -> {
            }

            is AccessTokenState.Loaded -> {
                if (!state.token.isNullOrEmpty()) {
                    stack.lastOrNull()?.let { item ->
                        UserProfileStackItemView(item) {
                            userHandle = it
                        }
                    }
                } else {
                    SignView()
                }
            }
        }
    }
}

@Composable
fun UserProfileStackItemView(
    item: UserProfileStackItem,
    updateUserHandle: (String) -> Unit
) {
    when (item) {
        is UserProfileStackItem.UserProfile -> UserProfileView(item.store, updateUserHandle)
        is UserProfileStackItem.MoatDetail -> MoatView(item.store)
    }
}

































