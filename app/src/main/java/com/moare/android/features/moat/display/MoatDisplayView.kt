package com.moare.android.features.moat.display

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.features.moat.display.view.MoatFormView
import com.moare.android.features.moat.display.view.MoatView
import com.moare.android.features.sign.display.store.SignStore
import com.moare.android.features.sign.display.view.SignView
import com.moare.android.ui.components.BackButton

@Composable
fun MoatDisplayView(
    stackStore: MoatStackStore,
    signStore: SignStore?
) {
//    val accessToken by viewModel.accessToken.collectAsState(initial = null)
    val accessTokenState by stackStore.accessTokenState.collectAsState()
    val stack by stackStore.stack.collectAsState()
    val isBootstrapped by stackStore.isBootstrapped.collectAsState()

    // NOTE: 처음 시작할때 accessToken이 안가져와짐
//    LaunchedEffect(Unit) {
//        if (!accessToken.isNullOrEmpty()) {
//            viewModel.send(MoatStackAction.BootstrapSession)
//        }
//    }
    LaunchedEffect(accessTokenState) {
        when (val state = accessTokenState) {
            AccessTokenState.Loading -> {
            }
            is AccessTokenState.Loaded -> {
                val token = state.token

                if (!isBootstrapped && !token.isNullOrBlank()) {
                    stackStore.send(MoatStackAction.BootstrapSession)
                    return@LaunchedEffect
                } else {
                    stackStore.send(MoatStackAction.InitSignStore)
                }

                if (token.isNullOrBlank()) {
                    stackStore.send(MoatStackAction.EmptyStack)
                } else {
                    if (stack.isEmpty()) {
                        stackStore.send(MoatStackAction.Push(MoatViewType.TRENDING))
                    }
                }
            }
        }
    }

    BackHandler {
        stackStore.send(MoatStackAction.Pop)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                stackStore.send(MoatStackAction.Pop)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "로그아웃",
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable {
                        stackStore.logout()
                    }
            )
        }

        when (val state = accessTokenState) {
            AccessTokenState.Loading -> {
            }

            is AccessTokenState.Loaded -> {
                if (!state.token.isNullOrEmpty()) {
                    stack.lastOrNull()?.let { item ->
                        MoatStackItemView(
                            item = item
                        )
                    }
                } else {
                    signStore?.let {
                        SignView(it)
                    }
                }
            }
        }
    }
}

@Composable
fun MoatStackItemView(
    item: MoatStackItem
) {
    when (item) {
        is MoatStackItem.Trending -> MoatView(item.store)
        is MoatStackItem.Detail -> MoatView(item.store)
        is MoatStackItem.CreateForm -> MoatFormView()
        is MoatStackItem.UpdateForm -> MoatFormView()
    }
}
















