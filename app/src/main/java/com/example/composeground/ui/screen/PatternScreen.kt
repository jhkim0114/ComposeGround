package com.example.composeground.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PatternScreen() {
    PatternLockScreen()
}

@Composable
fun PatternLockScreen() {
    val gridSize = 3
    val gridPoints = remember { mutableStateListOf<Offset>() }
    val selectedPoints = remember { mutableStateListOf<Int>() }
    val path = remember { Path() }

    var currentPoint by remember { mutableStateOf<Offset?>(null) }

    val spacing: Dp = 100.dp
    val offset: Dp = 50.dp
    val pointRadius: Dp = 10.dp

    // Density로 Dp를 Px로 변환
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { touchOffset ->
                            gridPoints.forEachIndexed { index, point ->
                                // 터치 시작 위치에 가까운 점을 감지
                                if (touchOffset.distanceTo(point) < pointRadius.toPx() * 2) {
                                    if (!selectedPoints.contains(index)) {
                                        selectedPoints.add(index)
                                        path.moveTo(point.x, point.y)
                                    }
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            currentPoint = change.position
                            // 가장 가까운 점 찾기
                            val closestPointIndex = findClosestPointIndex(change.position, gridPoints, selectedPoints, pointRadius.toPx())
                            closestPointIndex?.let { index ->
                                if (!selectedPoints.contains(index)) {
                                    selectedPoints.add(index)
                                    path.lineTo(gridPoints[index].x, gridPoints[index].y)
                                }
                            }
                        },
                        onDragEnd = {
                            currentPoint = null
                            path.reset()
                            selectedPoints.clear()
                        }
                    )
                }
        ) {
            // 그리드 점 배치
            for (i in 0 until gridSize) {
                for (j in 0 until gridSize) {
                    val pointIndex = i * gridSize + j

                    // Dp를 Px로 변환
                    val pointOffset = with(density) {
                        Offset(
                            x = offset.toPx() + j * spacing.toPx(),
                            y = offset.toPx() + i * spacing.toPx()
                        )
                    }

                    if (gridPoints.size <= pointIndex) {
                        gridPoints.add(pointOffset)
                    }

                    val isSelected = selectedPoints.contains(pointIndex)

                    drawCircle(
                        color = if (isSelected) Color.Blue else Color.Gray,
                        radius = pointRadius.toPx(),
                        center = pointOffset
                    )
                }
            }

            // 패턴 경로 그리기
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 8f)
            )
        }
    }
}

// 두 점 사이의 거리 계산
private fun Offset.distanceTo(other: Offset): Float {
    return Math.hypot(
        (this.x - other.x).toDouble(),
        (this.y - other.y).toDouble()
    ).toFloat()
}

// 가장 가까운 점의 인덱스를 찾는 함수
private fun findClosestPointIndex(
    currentPosition: Offset,
    gridPoints: List<Offset>,
    selectedPoints: List<Int>,
    radius: Float
): Int? {
    var closestIndex: Int? = null
    var closestDistance = Float.MAX_VALUE

    gridPoints.forEachIndexed { index, point ->
        if (!selectedPoints.contains(index)) { // 이미 선택된 점은 제외
            val distance = currentPosition.distanceTo(point)
            if (distance < closestDistance && distance < radius * 2) {
                closestIndex = index
                closestDistance = distance
            }
        }
    }
    return closestIndex
}