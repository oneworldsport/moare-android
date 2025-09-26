package com.moare.android.features.moat.display.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moare.android.ui.theme.Moare

@Composable
fun CommentComposer(
    text: String,
    onTextChange: (String) -> Unit,
    action: () -> Unit,
    modifier: Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text("모트 작성")
        },
        trailingIcon = {
            TextButton(
                onClick = action,
                enabled = text.isNotBlank()
            ) {
                Text("작성")
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Moare,
            unfocusedBorderColor = Moare
        )
    )
}