package com.example.composeground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.composeground.ui.screen.AutoSizeScreen
import com.example.composeground.ui.screen.TestGround
import com.example.composeground.ui.theme.ComposeGroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeGroundTheme(
                darkTheme = false
            ) {
                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = Color.Unspecified
                ) {
//                    AmountScrollScreen()
//                    GiftSendNormalScreen()
//                    ChatBotScreen()
//                    PatternScreen()
//                    ForexBoxHomeScreen()
//                    MmfAccountSettingScreen()
//                    CaptureScreen()
//                    PagingScreen(TestViewModel())
//                    LazyColumnScreen()
//                    ConstraintsScreen()
//                    ProgressScreen()
//                    TermsAgreementScreen()
//                    AutoSizeScreen()
                    TestGround()
                }
            }
        }
    }
}