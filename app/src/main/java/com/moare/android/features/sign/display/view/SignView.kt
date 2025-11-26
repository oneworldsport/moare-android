package com.moare.android.features.sign.display.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.sign.display.components.IdTypeSelectButton
import com.moare.android.features.sign.display.components.SelectedSports
import com.moare.android.features.sign.display.components.SportList
import com.moare.android.features.sign.display.store.SignFlow
import com.moare.android.features.sign.display.store.SignAction
import com.moare.android.features.sign.display.store.SignActivatedState
import com.moare.android.features.sign.display.store.SignViewModel
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.screenWidthDp

@Composable
fun SignView(
    viewModel: SignViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val fullWidth = screenWidthDp()

    val currentFlow by viewModel.currentFlow.collectAsState()
    val idType by viewModel.idType.collectAsState()
    val idTypeSelectedIndex by viewModel.idTypeSelectedIndex.collectAsState()

    val title by viewModel.title.collectAsState()
    val text by viewModel.text.collectAsState()
    val placeholder by viewModel.placeholder.collectAsState()
    val submitBtnLabel by viewModel.submitBtnLabel.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isTextFieldEnabled by viewModel.isTextFieldEnabled.collectAsState()
    val activatedState by viewModel.activatedState.collectAsState()
    val barAlignment by viewModel.barAlignment.collectAsState()
    val barWidth by viewModel.barWidth.collectAsState()
    val barDuration by viewModel.barDuration.collectAsState()
    val apiFetchState by viewModel.apiFetchState.collectAsState()
    val sportsInterests by viewModel.sportsInterests.collectAsState()

    val animationBarWidth by animateDpAsState(
        targetValue = barWidth,
        animationSpec = tween(
            durationMillis = barDuration,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.send(SignAction.SetFullWidth(fullWidth))
    }

    LaunchedEffect(isTextFieldEnabled) {
        if (isTextFieldEnabled) {
            focusRequester.requestFocus()
        }
    }

    CenterColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Title
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            this@CenterColumn.AnimatedVisibility(
                visible = currentFlow == SignFlow.LOGIN_ID || currentFlow == SignFlow.SIGN_UP_ID,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CenterRow {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = if (currentFlow == SignFlow.LOGIN_ID) "회원가입" else "로그인",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable {
                                if (currentFlow == SignFlow.LOGIN_ID) {
                                    viewModel.send(SignAction.UpdateSignFlow(SignFlow.SIGN_UP_ID))
                                } else if (currentFlow == SignFlow.SIGN_UP_ID) {
                                    viewModel.send(SignAction.UpdateSignFlow(SignFlow.LOGIN_ID))
                                }
                            }
                    )
                }
            }
        }

        if (currentFlow == SignFlow.SIGN_UP_SPORTS_INTERESTS) {
            Text(
                text = "보는거나 하는걸 즐기는 스포츠들을 선택해 주세요",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
        }

        // 이메일, 전화번호 선택 버튼
        AnimatedVisibility(
            visible = currentFlow == SignFlow.LOGIN_ID || currentFlow == SignFlow.SIGN_UP_ID,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            IdTypeSelectButton(idTypeSelectedIndex) { index ->
                viewModel.send(SignAction.SelectIdType(index))
            }
        }

        // 입력창, 확인 버튼
        CenterRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentFlow == SignFlow.SIGN_UP_SPORTS_INTERESTS) {
                SelectedSports(sports = sportsInterests ?: emptyList(), modifier = Modifier.weight(1f))
            } else {
                BasicTextField(
                    value = text,
                    onValueChange = { newValue ->
                        viewModel.send(SignAction.UpdateText(newValue))
                    },
                    singleLine = true,
//                keyboardOptions = KeyboardOptions(
//                    imeAction = ImeAction.
//                ),
//                keyboardActions = KeyboardActions(
//                    onSend = {
//
//                    }
//                )
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    enabled = isTextFieldEnabled,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .weight(1f),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        ) {
                            if (text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            if (activatedState == SignActivatedState.ALL_ACTIVATED || activatedState == SignActivatedState.ONLY_BUTTON_ACTIVATED) {
                Text(
                    text = submitBtnLabel,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Moare)
                        .clickable {
                            viewModel.send(SignAction.Submit)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            } else {
                Text(
                    text = submitBtnLabel,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Gray)
                        .alpha(0.7f)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Column(
            horizontalAlignment = barAlignment,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(animationBarWidth)
                    .height(2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Moare)
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(20.dp)
        ) {
            if (
                (currentFlow == SignFlow.LOGIN_OTP || currentFlow == SignFlow.SIGN_UP_OTP) &&
                isTextFieldEnabled &&
                text.length != 6
            ) {
                // TODO: 숫자만 포함하게 정규식 추가
                Text(
                    text = "인증번호 6자리를 입력해 주세요.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = errorMessage,
                fontSize = 13.sp,
                color = Moare,
                textAlign = TextAlign.Center
            )
        }

        if (currentFlow == SignFlow.SIGN_UP_SPORTS_INTERESTS) {
            SportList(
                selectedSports = sportsInterests ?: emptyList(),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                viewModel.send(SignAction.AddSport(it))
            }
        }
    }
}














