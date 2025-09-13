package com.example.composeground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.fragment.app.FragmentActivity
import com.example.composeground.ui.screen.NavHostScreen
import com.example.composeground.ui.theme.ComposeGroundTheme

class MainActivity : FragmentActivity() {
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
                    NavHostScreen()
//                    TestGround()
                }
            }
        }
    }
}