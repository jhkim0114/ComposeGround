@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composeground.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TermsAgreementScreen() {
    TermsBottomSheet(
        onConfirm = {},
        onDismissRequest = {}
    )
}

data class Term(
    val id: String,
    val title: String,
    val required: Boolean = true,
    val checked: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsBottomSheet(
    onConfirm: (acceptedIds: List<String>) -> Unit,
    onDismissRequest: () -> Unit,
    termsInitial: List<Term> = listOf(
        Term("t1", "서비스 이용약관(필수)", required = true),
        Term("t2", "개인정보 수집 및 이용(필수)", required = true),
        Term("t3", "마케팅 정보 수신(선택)", required = false),
    ),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var terms by remember { mutableStateOf(termsInitial) }

    // 파생 상태
    val allChecked by remember(terms) { derivedStateOf { terms.all { it.checked } } }
    val anyChecked by remember(terms) { derivedStateOf { terms.any { it.checked } } }
    val triState by remember(terms) {
        derivedStateOf {
            when {
                allChecked -> ToggleableState.On
                anyChecked -> ToggleableState.Indeterminate
                else -> ToggleableState.Off
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        sheetMaxWidth = Dp.Unspecified
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 상단 타이틀
            Text(
                text = "약관 동의",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 2번째: 전체 동의
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .clickable(role = Role.Checkbox) {
                        val target = !allChecked
                        terms = terms.map { it.copy(checked = target) }
                    }
                    .padding(14.dp)
            ) {
                TriStateCheckbox(
                    state = triState,
                    onClick = {
                        val target = !allChecked
                        terms = terms.map { it.copy(checked = target) }
                    }
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("전체 동의", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "모든 약관에 동의합니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 3번째: 약관 리스트 (체크 가능)
            Text("약관 목록", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(terms, key = { it.id }) { term ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Checkbox) {
                                terms = terms.map {
                                    if (it.id == term.id) it.copy(checked = !term.checked) else it
                                }
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = term.checked,
                            onCheckedChange = { checked ->
                                terms = terms.map {
                                    if (it.id == term.id) it.copy(checked = checked) else it
                                }
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(term.title, style = MaterialTheme.typography.bodyLarge)
                                if (term.required) {
                                    Spacer(Modifier.width(6.dp))
                                } else {
                                    Spacer(Modifier.width(6.dp))
                                }
                            }
                            Text(
                                text = "자세히 보기",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider()
                }
            }

            Spacer(Modifier.height(16.dp))

            // 하단 동의 버튼 (모두 체크 시 활성 + 노란색)
            val enabled = allChecked
            val enabledYellow = Color(0xFFFFD400) // 노란색
            Button(
                onClick = { onConfirm(terms.filter { it.checked }.map { it.id }) },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enabled) enabledYellow else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (enabled) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text("동의하고 진행")
            }
        }
    }
}

@Composable
private fun AssistChipSmall(label: String) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label) }
    )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun TermsBottomSheetPreview() {
    MaterialTheme {
        // 프리뷰에선 시트를 바로 보여주기 어렵기 때문에, 본문만 감싸서 레이아웃 확인
        Surface {
            TermsBottomSheet(
                onConfirm = {},
                onDismissRequest = {}
            )
        }
    }
}