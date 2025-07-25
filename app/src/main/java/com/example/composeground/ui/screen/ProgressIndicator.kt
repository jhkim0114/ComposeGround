package com.example.composeground.ui.screen

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import timber.log.Timber

@Preview(showBackground = true)
@Composable
fun ProgressScreen(
    modifier: Modifier = Modifier,
    text1: String = "text1text1text1text1text1text1",
    text2: String? = "text12"
) {
    Box(
        modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        GradientProgressDemo()
    }
}

@Composable
fun GradientProgressDemo() {
    var progress by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(top = 200.dp)
            .padding(16.dp)
    ) {
        ProgressItem(progress = progress)
        Spacer(modifier = Modifier.height(30.dp))
        GradientProgressWithCorrectMarker(progress = progress)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            progress = if (progress >= 100f) 0f else progress + 20f
        }) {
            Text("진행도 증가")
        }
    }
}

@Composable
private fun ProgressItem(
    @FloatRange(0.0, 100.0)
    progress: Float
) {
    Column {
        var width by remember { mutableFloatStateOf(0f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress.coerceIn(0f, 100f) / 100,
            animationSpec = tween(durationMillis = 1000),
            label = "animatedProgress"
        )

        var markerWidth by remember { mutableFloatStateOf(0f) }

        val markerX = when {
            width * (animatedProgress) - (markerWidth / 2) < 0 -> {
                0f
            }

            width * (animatedProgress) + (markerWidth / 2) > width -> {
                width - markerWidth
            }

            else -> {
                width * (animatedProgress) - (markerWidth / 2)
            }
        }

        // marker
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            modifier = Modifier
                .onGloballyPositioned {
                    markerWidth = it.size.width.toFloat()
                }
                .graphicsLayer {
                    translationX = markerX
                }
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            width = size.width
            drawRoundRect(
                color = Color.LightGray,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(6.dp.toPx())
            )

            val circleRadius = 6.dp
            val circlePadding = 5.dp
            val points = listOf(
                Offset(0f + circleRadius.toPx() + circlePadding.toPx(), size.height / 2),
                Offset(size.width / 2, size.height / 2),
                Offset(size.width - circleRadius.toPx() - circlePadding.toPx(), size.height / 2)
            )

            points.forEach {
                drawCircle(color = Color.Blue, radius = circleRadius.toPx(), it)
            }

            if (animatedProgress > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color.Green,
                            1f to Color.Yellow
                        ),
                        endX = width * (animatedProgress)
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(width * (animatedProgress), size.height),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
            }

        }
    }
}

@Composable
fun GradientProgressWithCorrectMarker(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    cornerRadius: Dp = 6.dp,
    backgroundColor: Color = Color.LightGray,
    gradientColors: List<Color> = listOf(Color.Yellow, Color.Green),
    animationDuration: Int = 1000
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = animationDuration),
        label = "animatedProgress"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height + 50.dp) // 마커가 위로 뜨기 때문에 공간 확보
    ) {
        val fullWidth = size.width
        val barHeight = height.toPx()
        val progressX = fullWidth * animatedProgress
        val radius = cornerRadius.toPx()

        // 프로그레스 바 위치 (아래에 고정)
        val barY = size.height - barHeight

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(0f, barY),
            size = Size(fullWidth, barHeight),
            cornerRadius = CornerRadius(radius, radius)
        )

        if (animatedProgress > 0f) {
            val gaugeGradient = Brush.horizontalGradient(
                colors = gradientColors,
                startX = 0f,
                endX = progressX // 이게 포인트!
            )
            drawRoundRect(
                brush = gaugeGradient,
                topLeft = Offset(0f, barY),
                size = Size(progressX, barHeight),
                cornerRadius = CornerRadius(radius, radius)
            )
        }

        // 마커 위치 계산
        val markerWidth = 60.dp.toPx()
        val markerHeight = 28.dp.toPx()
        val triangleHeight = 6.dp.toPx()
        val markerGap = 20.dp.toPx() // 바와 마커 사이 거리

        val markerX = progressX.coerceIn(markerWidth / 2, fullWidth - markerWidth / 2)
        val markerOffsetY = barY - (markerHeight + triangleHeight + markerGap)

        // 마커 말풍선 경로
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(markerX - markerWidth / 2, markerOffsetY),
                        size = Size(markerWidth, markerHeight)
                    ),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            )
            // 꼬리
            moveTo(markerX - 8.dp.toPx(), markerOffsetY + markerHeight)
            lineTo(markerX, markerOffsetY + markerHeight + triangleHeight)
            lineTo(markerX + 8.dp.toPx(), markerOffsetY + markerHeight)
            close()
        }

        drawPath(path = path, color = Color.Black)

        // 텍스트
        val text = "Y: %.2f".format(animatedProgress)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 12.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            text,
            markerX,
            markerOffsetY + markerHeight / 2 + textPaint.textSize / 2 - 4.dp.toPx(),
            textPaint
        )
    }
}