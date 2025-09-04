package com.example.composeground.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AutoSizeScreen() {
    val text = "경기도 광주시 고불로\n힐스테이트삼동역"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = text,
            color = Color.Green,
            fontSize = 13.sp,
            maxLines = 2
        )
        BasicText(
            text = text,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp,
                maxFontSize = 13.sp,
                stepSize = 1.sp,
            ),
            color = ColorProducer { Color(0xff000000) },
            maxLines = 2
        )
    }
}
