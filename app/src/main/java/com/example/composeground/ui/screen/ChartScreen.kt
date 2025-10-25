package com.example.composeground.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChartScreen() {
    Text("차트")
    Column {
        Spacer(modifier = Modifier.weight(1f))
        GradientLineChartInteractiveWithLabel(
            dataPoints = listOf(12f, 28f, 18f, 52f, 42f, 73f, 65f, 12f, 28f, 18f, 52f, 42f, 73f, 65f),
            lineColor = Color(0xFF3F51B5),
            centerLabel = "평균",
            durationMillis = 1200,
            verticalLabel = { i, v -> "x:$i  y:${"%.1f".format(v)}" } // 커스텀 포맷
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun GradientLineChartInteractiveWithLabel(
    modifier: Modifier = Modifier,
    dataPoints: List<Float> = listOf(10f, 40f, 20f, 60f, 30f, 80f),
    lineColor: Color = Color(0xFF4CAF50),
    centerLabel: String = "평균",
    durationMillis: Int = 1200,
    topPaddingRatio: Float = 0.1f, // ✅ 상단 여유: 10%
    verticalLabel: (index: Int, value: Float) -> String = { _, v -> "%.1f".format(v) }
) {
    require(dataPoints.size >= 2) { "dataPoints must have at least 2 items" }

    // ----- 리빌 애니메이션 -----
    var play by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (play) 1f else 0f,
        animationSpec = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
        label = "revealProgress"
    )
    LaunchedEffect(Unit) { play = true }

    // ----- 인터랙션 상태 -----
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    fun snapIndex(x: Float) {
        if (canvasSize.width <= 0) return
        val stepX = canvasSize.width.toFloat() / (dataPoints.size - 1)
        selectedIndex = (0 until dataPoints.size).minByOrNull { i ->
            kotlin.math.abs(stepX * i - x)
        } ?: -1
    }

    val interactionModifier = Modifier
        .onSizeChanged { canvasSize = it }
        // 탭: 누르는 동안 표시, 손 떼면 제거
        .pointerInput(dataPoints) {
            detectTapGestures(
                onPress = { pos ->
                    snapIndex(pos.x)
                    try {
                        awaitRelease()
                    } finally {
                        selectedIndex = -1
                    }
                }
            )
        }
        // 드래그: 시작/이동 중 표시, 끝/캔슬 시 제거
        .pointerInput(dataPoints) {
            detectDragGestures(
                onDragStart = { pos -> snapIndex(pos.x) },
                onDrag = { change, _ -> snapIndex(change.position.x) },
                onDragEnd = { selectedIndex = -1 },
                onDragCancel = { selectedIndex = -1 }
            )
        }

    Canvas(
        modifier = modifier
            .then(interactionModifier)
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val maxY = dataPoints.maxOrNull() ?: 0f
        val minY = dataPoints.minOrNull() ?: 0f
        val rawRange = (maxY - minY).takeIf { it > 0f } ?: 1f
        val adjustedMaxY = maxY + rawRange * topPaddingRatio // ✅ 상단 여유 적용
        val stepX = size.width / (dataPoints.size - 1)
        val scaleY = size.height / (adjustedMaxY - minY)

        // ----- 경로 구성 (라인/채움) -----
        val linePath = Path()
        val fillPath = Path()
        dataPoints.forEachIndexed { index, v ->
            val x = stepX * index
            val y = size.height - (v - minY) * scaleY
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        val lastX = stepX * (dataPoints.size - 1)
        fillPath.lineTo(lastX, size.height)
        fillPath.close()

        // ----- 그라데이션 (위→아래: 선색 → 투명) -----
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(lineColor.copy(alpha = 0.4f), lineColor.copy(alpha = 0f)),
            startY = 0f, endY = size.height
        )

        // ----- 리빌 클리핑 -----
        val revealRight = size.width * progress
        clipRect(left = 0f, top = 0f, right = revealRight, bottom = size.height) {
            drawPath(path = fillPath, brush = gradientBrush, style = Fill)
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // ----- 중앙 점선 -----
        val centerY = size.height / 2f
        val dashWidth = 10.dp.toPx()
        val gapWidth = 6.dp.toPx()
        var currentX = 0f
        while (currentX < size.width) {
            val endX = (currentX + dashWidth).coerceAtMost(size.width)
            drawLine(
                color = Color.LightGray,
                start = Offset(currentX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = 1.dp.toPx()
            )
            currentX += (dashWidth + gapWidth)
        }
        // 중앙 라벨 (점선 왼쪽 위)
        run {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 12.sp.toPx()
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.LEFT
            }
            drawContext.canvas.nativeCanvas.drawText(
                centerLabel,
                4.dp.toPx(),
                centerY - 8.dp.toPx(),
                paint
            )
        }

        // ----- 수직 라인 + 상단 오른쪽 텍스트 (터치 중에만) -----
        if (selectedIndex in 0 until dataPoints.size) {
            val xSel = stepX * selectedIndex
            if (xSel <= revealRight) {
                // 수직 라인
                drawLine(
                    color = Color.Gray.copy(alpha = 0.9f),
                    start = Offset(xSel, 0f),
                    end = Offset(xSel, size.height),
                    strokeWidth = 1.5.dp.toPx()
                )

                // 라벨 텍스트(상단 오른쪽)
                val value = dataPoints[selectedIndex]
                val label = verticalLabel(selectedIndex, value)
                val labelPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 12.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.LEFT
                }
                val paddingX = 6.dp.toPx()
                val paddingTop = 12.sp.toPx() + 4.dp.toPx()
                val textWidth = labelPaint.measureText(label)

                val textX = if (xSel + paddingX + textWidth <= size.width) {
                    xSel + paddingX
                } else {
                    xSel - paddingX - textWidth
                }
                val textY = paddingTop
                drawContext.canvas.nativeCanvas.drawText(label, textX, textY, labelPaint)
            }
        }
    }
}