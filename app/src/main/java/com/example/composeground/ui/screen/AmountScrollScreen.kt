package com.example.composeground.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composeground.ui.widget.AmountScrollView

@Composable
fun AmountScrollScreen() {
    Box {
        Divider(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .align(Alignment.Center),
            color = Color.Red
        )
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text("title")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AmountScrollView()
            }
        }
    }
}