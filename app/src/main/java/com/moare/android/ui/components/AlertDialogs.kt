package com.moare.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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