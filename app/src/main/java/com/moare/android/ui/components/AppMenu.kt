package com.moare.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppMenu(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    items: List<String>,
    onItemSelected: (index: Int, label: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
//        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Box(
            modifier = Modifier.clickable { expanded = true }
        ) {
            label()
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, title ->
                DropdownMenuItem(
                    text = { Text(text = title) },
                    onClick = {
                        onItemSelected(index, title)
                        expanded = false
                    }
                )
            }
        }
    }
}