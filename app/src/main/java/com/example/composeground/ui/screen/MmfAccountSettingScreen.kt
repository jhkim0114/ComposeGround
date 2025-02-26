package com.example.composeground.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MmfAccountSettingScreen() {
    InvestmentBoxScreen()
}

@Preview
@Composable
fun InvestmentBoxScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection()
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            InfoSection()
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            SettingsSection()
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            QuerySection()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderSection() {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "투자박스",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "3333-02-1234567",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InfoSection() {
    Column(modifier = Modifier.padding(8.dp)) {
        InfoRow(label = "펀드명", value = "NH-Amundi개인신종MMF1호 Ce")
        InfoRow(label = "개설일", value = "2024.10.10")
        InfoRow(label = "입금한도", value = "50,000,000원")
        InfoRow(label = "입금액", value = "1,349,000원")
        InfoRow(label = "과세구분", value = "일반과세")
    }
}

@Preview(showBackground = true)
@Composable
fun InfoRow(label: String = "라벨", value: String = "값") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SettingsSection() {
    Column(modifier = Modifier.padding(8.dp)) {
        SettingItem(label = "연결계좌", value = "김이지의 통장(1234)")
        SettingItem(label = "출금예약 관리", value = "")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingItem(label: String = "라벨", value: String = "값") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Add navigation or action */ }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuerySection() {
    Column(modifier = Modifier.padding(8.dp)) {
        QueryItem(label = "실질수익률")
        QueryItem(label = "통장사본")
        QueryItem(label = "가입서류")
        QueryItem(label = "투자성향")
    }
}

@Preview(showBackground = true)
@Composable
fun QueryItem(label: String = "라벨") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Add navigation or action */ }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}