package com.example.composeground.ui.screen

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.composeground.R
import com.example.composeground.ui.theme.ForexBoxMainColor
import com.example.composeground.viewmodel.ForexBoxHomeUiState
import com.example.composeground.viewmodel.ForexBoxHomeViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@Composable
fun ForexBoxHomeScreen(viewModel: ForexBoxHomeViewModel = ForexBoxHomeViewModel()) {
    val view = LocalView.current
    val window = (view.context as ComponentActivity).window
    window.statusBarColor = ForexBoxMainColor.toArgb()
    val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
    windowInsetsController.isAppearanceLightStatusBars = true

    Column {
        ForexBoxNavigationBar()
        ForexBoxContentView(viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun ForexBoxNavigationBar() {
    val coroutineScrope = rememberCoroutineScope()
    val localContext = LocalContext.current

    Box(
        modifier = Modifier.background(ForexBoxMainColor)
    ) {
        Icon(
            Icons.Rounded.ArrowBack,
            null,
            modifier = Modifier
                .clickable {
                    coroutineScrope.launch {
                        Toast
                            .makeText(localContext, "클릭?", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .padding(10.dp),
            tint = Color.Black
        )
        Text(
            "달러박스",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForexBoxContentView(viewModel: ForexBoxHomeViewModel = ForexBoxHomeViewModel()) {
    val data = viewModel.uiState.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ForexBoxMainColor)
    ) {
        data.value?.let {
            when (it) {
                is ForexBoxHomeUiState.Loading -> {
                    AmountBoxLayout(
                        modifier = Modifier.align(Alignment.TopCenter), isLoading = true
                    )
                }

                is ForexBoxHomeUiState.Success -> {
                    AmountBoxLayout(
                        modifier = Modifier.align(Alignment.TopCenter), isLoading = false
                    )
                }

                is ForexBoxHomeUiState.Error -> {

                }
            }

        }
    }
}


// composed - Modifier의 특정 부분을 기억하여 성능 최적화 및 리컴포지션의 부하를 줄이기 위해 사용
fun Modifier.shimmerEffect(): Modifier = composed {
    // 컴포저블의 현재 크기를 저장
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(), // 애니메이션 시작 위치 (왼쪽 바깥)
        targetValue = 2 * size.width.toFloat(),   // 애니메이션 끝 위치 (오른쪽 바깥)
        animationSpec = infiniteRepeatable(       // 무한 반복 애니메이션 설정
            animation = tween(1000)
        ),
        label = ""
    )

    // 선형 그라데이션 브러시로 배경을 설정
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFffffff),
                Color(0xFFDCDCDC),
                Color(0xFFA9A9A9),
                Color(0xFFDCDCDC),
                Color(0xFFffffff)
            ),
            start = Offset(startOffsetX, 0f),     // 그라데이션 시작 지점 (x축 이동)
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()) // 그라데이션 끝 지점
        )
    )
        // 컴포저블이 배치될 때 크기를 얻어 상태에 저장
        .onGloballyPositioned { layoutCoordinates ->
            size = layoutCoordinates.size             // 현재 컴포저블의 크기를 상태에 저장
        }
}

@Preview(showBackground = true)
@Composable
fun AmountBoxLayout(modifier: Modifier = Modifier, isLoading: Boolean = false) {
    var isAnimationFinished by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(top = 50.dp)
            .size(300.dp)
            .clip(CircleShape)
            .background(Color.White)
            .let { baseModifier ->
                if (isLoading) {
                    baseModifier.shimmerEffect()
                } else {
                    baseModifier
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) return@Box
        if (isAnimationFinished) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { "https://img1.daumcdn.net/thumb/R1280x0.fjpg/?fname=http://t1.daumcdn.net/brunch/service/user/cnoC/image/4yPtuRXtR0-jusOMCCXb4MeN6zU.jpg" }
            )
        } else {
            LottieAnimationView(
                onAnimationEnd = {
                    isAnimationFinished = true
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LottieAnimationView(onAnimationEnd: () -> Unit = {}) {
    // Lottie 애니메이션 구성
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))

    // 상태설정?
    val animationState by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
//        iterations = LottieConstants.IterateForever // 무한 반복
    )

    // 1f 완료?
    LaunchedEffect(animationState) {
        if (animationState == 1f) {
            onAnimationEnd() // 완료 다음 화면 처리
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { animationState },
    )
}
