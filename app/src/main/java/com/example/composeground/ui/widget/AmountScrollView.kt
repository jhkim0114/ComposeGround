package com.example.composeground.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * *** 구현
 * 베이스? LazyRow
 * 아이템 정보 listState.layoutInfo.visibleItemsInfo.firstOrNull()
 * firstVisibleItemScrollOffset로 어디로 이동할지 확인
 *
 * *** 이슈
 * 가운데로 보내기? contentPadding
 * snapshotFlow 반복 호출 문제? collect 안에서 animateScrollToItem 하는 문제로 분리
 * 같은 index 아이템에서는 animateScrollToItem 안하는 문제? trigger key값 추가
 */
@Preview(showBackground = true)
@Composable
fun AmountScrollView(
    min: Long = 100,
    max: Long = 5600,
    interval: Long = 100
) {
    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    var triggerAnimation by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(selectedIndex, triggerAnimation) {
        listState.animateScrollToItem(selectedIndex)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { !it }
            .collect {
                val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                if (firstVisibleItem != null) {
                    val itemWidth = firstVisibleItem.size
                    selectedIndex =
                        if (itemWidth / 2 > listState.firstVisibleItemScrollOffset) {
                            listState.firstVisibleItemIndex
                        } else {
                            listState.firstVisibleItemIndex + 1
                        }
                    triggerAnimation++
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val amountList = (min..max).filter {
            it % interval == 0L
        }
        val screenWithHalfSize = LocalConfiguration.current.screenWidthDp / 2

        if (amountList.size > firstVisibleItemIndex) {
            Text(amountList[firstVisibleItemIndex].toString(), fontSize = 50.sp)
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = screenWithHalfSize.dp),
        ) {
            items(amountList, key = {it}) {item ->
                Row {
                    Canvas(
                        modifier = Modifier
                            .width(2.5.dp)
                            .height(150.dp)
                    ) {

                        val rectWidth = 2.5f
                        val rectHeight = 150f
                        val cornerRadius = rectWidth / 2f

                        val left = (size.width - rectWidth) / 2
                        val top = (size.height - rectHeight) / 2

                        drawRoundRect(
                            color = Color.Gray,
                            topLeft = Offset(left, top),
                            size = Size(rectWidth, rectHeight),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                }
            }
        }
    }
}