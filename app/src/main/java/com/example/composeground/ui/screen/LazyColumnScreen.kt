@file:OptIn(ExperimentalFoundationApi::class)

package com.example.composeground.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed interface MmfAccountHistoryUiState {

    data class Success(val title: String) : MmfAccountHistoryUiState

    data object Loading : MmfAccountHistoryUiState

    data object Failure : MmfAccountHistoryUiState

    data object Empty : MmfAccountHistoryUiState
}

@Preview(showBackground = true)
@Composable
fun LazyColumnScreen(
//    uiState: MmfAccountHistoryUiState = MmfAccountHistoryUiState.Success("")
    uiState: MmfAccountHistoryUiState = MmfAccountHistoryUiState.Loading
) {
//    Column {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            item {
//                Text("Header", fontSize = 50.sp)
//            }
//            stickyHeader {
//                Box(
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    Text(
//                        "Item",
//                        fontSize = 50.sp,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(color = Color.Blue)
//                    )
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(40.dp)
//                            .offset(y = 40.dp)
//                            .background(
//                                brush = Brush.verticalGradient(
//                                    colors = listOf(Color.Yellow, Color.Transparent)
//                                )
//                            )
//                    )
//                }
//            }
//
//            when (uiState) {
//                is MmfAccountHistoryUiState.Success -> {
//                    item {
//                        repeat(30) {
//                            Text("Item $it", fontSize = 50.sp)
//                        }
//                    }
//                }
//
//                MmfAccountHistoryUiState.Loading,
//                MmfAccountHistoryUiState.Failure,
//                MmfAccountHistoryUiState.Empty -> {
//                    item {
//                        Box(
//                            modifier = Modifier.fillParentMaxSize().fillMaxHeight(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("empty")
//                        }
//                    }
//                }
//            }
//        }
//    }

    Column {
//        Spacer(modifier = Modifier.fillMaxWidth().height(500.dp).background(color = Color.Yellow))
        var maxLazyColumnHeight by remember { mutableStateOf(0.dp) }
        var headerTitleHeight by remember { mutableStateOf(0.dp) }
        var chipGroupHeight by remember { mutableStateOf(0.dp) }
        val otherHeight by remember {
            derivedStateOf { maxLazyColumnHeight - headerTitleHeight - chipGroupHeight }
        }

        val density = LocalDensity.current
        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .onPlaced { coordinates ->
                    maxLazyColumnHeight = with(density) {
                        coordinates.size.height.toDp()
                    }
                }
        ) {
            item {
                Text("Header", fontSize = 50.sp, modifier = Modifier.onPlaced { coordinates ->
                    headerTitleHeight = with(density) {
                        coordinates.size.height.toDp()
                    }
                })
            }
            stickyHeader {
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        "Item",
                        fontSize = 50.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Blue)
                            .onPlaced { coordinates ->
                                chipGroupHeight = with(density) {
                                    coordinates.size.height.toDp()
                                }
                            }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .offset(y = 40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Yellow, Color.Transparent)
                                )
                            )
                    )
                }
            }

            when (uiState) {
                is MmfAccountHistoryUiState.Success -> {
                    item {
                        repeat(30) {
                            Text("Item $it", fontSize = 50.sp)
                        }
                    }
                }

                MmfAccountHistoryUiState.Loading,
                MmfAccountHistoryUiState.Failure,
                MmfAccountHistoryUiState.Empty -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(otherHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("empty")
                        }
                    }
                }
            }
        }
    }

}