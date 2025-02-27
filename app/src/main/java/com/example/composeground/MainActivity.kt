package com.example.composeground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeground.ui.screen.CaptureScreen
import com.example.composeground.ui.screen.LazyColumnScreen
import com.example.composeground.ui.screen.PagingScreen
import com.example.composeground.ui.theme.ComposeGroundTheme
import com.example.composeground.viewmodel.TestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeGroundTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
//                    AmountScrollScreen()
//                    GiftSendNormalScreen()
//                    ChatBotScreen()
//                    PatternScreen()
//                    ForexBoxHomeScreen()
//                    MmfAccountSettingScreen()
//                    CaptureScreen()
//                    PagingScreen(TestViewModel())
                    LazyColumnScreen()
                }
            }
        }
    }
}