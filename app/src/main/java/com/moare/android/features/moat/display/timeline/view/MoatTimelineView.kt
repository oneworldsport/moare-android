package com.moare.android.features.moat.display.timeline.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.moat.display.timeline.viewmodel.MoatTimelineViewModel
import com.moare.android.features.sign.display.signin.common.IdTypeSelectButton
import com.moare.android.features.sign.display.signin.view.SignView
import com.moare.android.features.sign.display.signin.viewmodel.SignFlow
import com.moare.android.features.sign.display.signin.viewmodel.SignIntent
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun MoatTimelineView(
    moatTimelineViewModel: MoatTimelineViewModel = hiltViewModel()
) {
    val accessToken by moatTimelineViewModel.accessToken.collectAsState(initial = null)

    if (!accessToken.isNullOrEmpty()) {
        CenterColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            Text("환영~~~")
        }
    } else {
        // 로그인 안 되어있을 때
        SignView()
    }
}