package com.moare.android.features.sign.display.signin.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.sign.display.signin.common.IdTypeSelectButton
import com.moare.android.features.sign.display.signin.viewmodel.SignFlow
import com.moare.android.features.sign.display.signin.viewmodel.SignIntent
import com.moare.android.features.sign.display.signin.viewmodel.SignViewModel
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay

@Composable
fun SignView(
    signViewModel: SignViewModel = hiltViewModel()
) {
    val focusRequester = remember { FocusRequester() }
    val fullWidth = screenWidthDp()

    val currentFlow by signViewModel.currentFlow.collectAsState()
    val title by signViewModel.title.collectAsState()
    val idTypeSelectedIndex by signViewModel.idTypeSelectedIndex.collectAsState()
    val idType by signViewModel.idType.collectAsState()
    val text by signViewModel.text.collectAsState()
    val placeholder by signViewModel.placeholder.collectAsState()
    val submitBtnLabel by signViewModel.submitBtnLabel.collectAsState()
    val isValid by signViewModel.isValid.collectAsState()
    val errorText by signViewModel.errorText.collectAsState()
    val apiFetchState by signViewModel.apiFetchState.collectAsState()
    val isCheckingNickname by signViewModel.isCheckingNickname.collectAsState()

    val isTextFieldEnabled = currentFlow != SignFlow.LOGIN_OTP_EXPIRED && currentFlow != SignFlow.LOGIN_OTP_LIMIT_EXCEEDED
    val barWidth = remember { Animatable(20.dp, Dp.VectorConverter) }
    var barAlignment by remember { mutableStateOf(Alignment.Start) }
    val errorTextAlpha by animateFloatAsState(
        targetValue = if (apiFetchState is ApiFetchState.Error) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isValid) {
        barWidth.animateTo(
            targetValue = if (isValid) fullWidth else 20.dp,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(isTextFieldEnabled) {
        if (isTextFieldEnabled) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(apiFetchState) {
        if (isCheckingNickname) {
            // NOTE: Nickname check할때는 fetching 할때 progress bar가 반대로 움직여야해서 해당 분기 추가
            // 성공 후에는 ApiFetchState.Success가 아니라 isValid를 통해 progress bar를 채운다.
            if (apiFetchState == ApiFetchState.Fetching) {
                barWidth.animateTo(
                    targetValue = fullWidth,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = FastOutSlowInEasing
                    )
                )
            } else if (apiFetchState is ApiFetchState.Error) {
                barWidth.animateTo(
                    targetValue = 20.dp,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        } else {
            if (apiFetchState == ApiFetchState.Fetching) {
                barAlignment = if (barAlignment == Alignment.Start) {
                    Alignment.End
                } else {
                    Alignment.Start
                }

                barWidth.animateTo(
                    targetValue = 20.dp,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = FastOutSlowInEasing
                    )
                )
            } else if (apiFetchState == ApiFetchState.Success) {
                barWidth.animateTo(
                    targetValue = 20.dp,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            } else if (
                apiFetchState is ApiFetchState.Error
                && (currentFlow == SignFlow.LOGIN_OTP_EXPIRED || currentFlow == SignFlow.LOGIN_OTP_LIMIT_EXCEEDED)
            ) {
                barWidth.animateTo(
                    targetValue = fullWidth,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
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

            // 회원가입 버튼
            this@CenterColumn.AnimatedVisibility(
                visible = currentFlow == SignFlow.LOGIN_ID,
                exit = fadeOut()
            ) {
                Text(
                    text = "회원가입",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .clickable {
                            signViewModel.send(SignIntent.UpdateSignFlow(SignFlow.SIGN_UP_ID))
                        }
                        .fillMaxWidth()
                )
            }
        }

        // 이메일, 전화번호 선택 버튼
        AnimatedVisibility(
            visible = currentFlow == SignFlow.LOGIN_ID || currentFlow == SignFlow.SIGN_UP_ID,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            IdTypeSelectButton(idTypeSelectedIndex) { index ->
                signViewModel.send(SignIntent.SelectIdType(index))
            }
        }

        // 입력창, 확인 버튼
        Column(
            horizontalAlignment = barAlignment
        ) {
            CenterRow {
                if (currentFlow != SignFlow.SIGN_UP_SPORTS_INTERESTS) {
                    BasicTextField(
                        value = text,
                        onValueChange = { newValue ->
                            signViewModel.send(SignIntent.UpdateText(newValue))
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
                        enabled = currentFlow != SignFlow.LOGIN_OTP_EXPIRED && currentFlow != SignFlow.LOGIN_OTP_LIMIT_EXCEEDED,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .weight(1f),
//                        .height(50.dp)
//                        .padding(bottom = 4.dp),
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

                Text(
                    text = submitBtnLabel,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isValid) Moare else Color.Gray)
                        .clickable {
                            signViewModel.send(SignIntent.Submit)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            if (currentFlow != SignFlow.SIGN_UP_SPORTS_INTERESTS) {
                Box(
                    modifier = Modifier
                        .width(barWidth.value)
                        .height(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Moare)
                )
            }
        }

        Text(
            text = errorText,
            fontSize = 13.sp,
            color = Moare,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(errorTextAlpha)
                .padding(top = 6.dp)
        )
    }
}