@file:OptIn(ExperimentalFoundationApi::class)

package com.example.composeground.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LazyColumnScreen() {

//    val lazyListState = rememberLazyListState()
//
//    Column {
//        Text(
//            text = "Header",
//            fontSize = 50.sp,
//            modifier = Modifier
//                .fillMaxWidth()
//        )
//        Text(
//            text = "Item",
//            fontSize = 50.sp,
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(color = Color.Blue)
//        )
//        Box {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//                    .background(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(Color.Yellow, Color.Transparent)
//                        )
//                    )
//            )
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                state = lazyListState
//            ) {
//                repeat(30) {
//                    item {
//                        Text("Item $it", fontSize = 50.sp)
//                    }
//                }
//            }
//        }
//    }


    LazyColumn {
        item {
            Text("Header", fontSize = 50.sp)
        }
        stickyHeader {
            Text(
                "Item",
                fontSize = 50.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Blue)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Yellow, Color.Transparent)
                        )
                    )
                    .graphicsLayer {
                        translationY = (-40).dp.toPx()
                    }
            )
        }
        repeat(30) {
            if (it == 0) {
                item {
                    Text("Item $it", fontSize = 50.sp)
                }
            } else {
                item {
                    Text("Item $it", fontSize = 50.sp)
                }
            }
        }
    }
}