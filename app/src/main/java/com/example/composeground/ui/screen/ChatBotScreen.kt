package com.example.composeground.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ChatBotScreen() {
    Column {
        NavigationBar()
        ContentView()
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBar() {
    val coroutineScrope = rememberCoroutineScope()
    val localContext = LocalContext.current

    Box {
        Icon(
            Icons.Rounded.Close,
            null,
            modifier = Modifier.clickable {
                coroutineScrope.launch {
                    Toast.makeText(localContext, "클릭?", Toast.LENGTH_SHORT).show()
                }
            }.padding(10.dp)
        )
        Text(
            "투자성향",
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContentView() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("내용")
    }
}


