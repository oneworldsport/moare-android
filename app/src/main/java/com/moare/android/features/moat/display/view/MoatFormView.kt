package com.moare.android.features.moat.display.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.moat.display.store.MoatIntent
import com.moare.android.features.moat.display.store.MoatViewModel

@Composable
fun MoatFormView(
    moatViewModel: MoatViewModel = hiltViewModel(),
) {
    var text by rememberSaveable { mutableStateOf("") }
    
    val hashtagList = listOf("#축구", "#농구", "#야구", "#테니스")

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "모트 작성",
            )

            Text(
                text = "첫번째 줄은 메인에서 주제(썸네일?)로 표시됩니다.",
                fontSize = 12.sp,
                color = Color.Gray
            )

            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue
                },
                modifier = Modifier.fillMaxWidth()
                    .height(100.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                items(hashtagList.size) { index ->
                    Text(
                        text = hashtagList[index],
                        modifier = Modifier.padding(horizontal = 4.dp)
                            .clickable(true, onClick = {})
                    )
                }
            }

            Button(
                onClick = { moatViewModel.send(MoatIntent.CreateMoat(text)) }
            ) {
                Text(
                    text = "작성하기",
                )
            }
        }
    }
}

@Preview
@Composable
fun FormViewPreview() {
//    FormView()
}