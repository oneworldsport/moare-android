package com.moare.android.features.moat.display.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moare.android.ui.theme.Moare

enum class SettingItems {
    REPORT, ONE, TWO, THREE, FOUR
}

@Composable
fun SettingWindow(
    reportTapped: () -> Unit,
) {
    LazyColumn(
    ) {
        items(SettingItems.entries.size) { index ->
            val title = SettingItems.entries[index].name
            TextButton(
                reportTapped,
                modifier = Modifier.fillMaxWidth().padding(start = 100.dp).background(Color(0xFFF7F7F7)),
            ) {
                Text(
                    text = title
                )
            }
        }
    }
}

@Composable
fun TextFieldAlert(
    isPresent: Boolean,
    onDismiss: () -> Unit
) {

    var text by rememberSaveable { mutableStateOf("") }

    if (isPresent) {
//        AlertDialog(
//            onDismissRequest = {  },
//            title = {
//                Text("모트 신고하기")
//            },
//            text = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(120.dp)
//                ) {
//                    OutlinedTextField(
//                        value = text,
//                        onValueChange = { text = it },
//                        singleLine = false,
//                        modifier = Modifier.fillMaxSize(),
//                        placeholder = {
//                            Text("신고할 내용을 작성해주세요")
//                        }
//                    )
//                }
//            },
//            buttons = {
//                Row(
//                    modifier = Modifier.padding(all = 8.dp).fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Button(
//                        onClick = { onDismiss() },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Dismiss")
//                    }
//
//                    Button(
//                        onClick = { onDismiss() },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("신고하기")
//                    }
//                }
//            }
//        )
        AlertDialog(
            onDismissRequest = {  },
            title = {
                Text("모트 신고하기")
            },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text("신고할 내용을 작성해주세요")
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text("신고하기")
                }
            },
            dismissButton = {
                Button(
                    onClick = { onDismiss() }
                ) {
                    Text("취소")
                }
            }
        )
    }
}