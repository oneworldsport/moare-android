package com.moare.android.features.sign.display.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.moare.android.features.sign.models.TermKey
import com.moare.android.features.sign.models.TermType
import com.moare.android.features.sign.models.TermsResponse
import com.moare.android.ui.theme.Moare

@Composable
fun SignUpTerms(
    terms: List<TermsResponse>,
    checked: Map<TermKey, Boolean>,
    onToggle: (TermKey, Boolean) -> Unit,
    onOpenTerm: (String) -> Unit
) {
    Column {
        terms.forEach { term ->
            val title = when (term.termType) {
                TermType.PRIVACY -> "(필수)개인정보 수집 및 이용 동의"
                else -> "(필수)이용약관 동의"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked[term.selfKey] == true,
                    onCheckedChange = { onToggle(term.selfKey, it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Moare,
                        uncheckedColor = Color(0xFF9E9E9E),
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onOpenTerm(term.url)
                    }
                ) {
                    Text(
                        text = title
                    )
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null
                    )
                }
            }
        }
    }
}