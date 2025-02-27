package com.example.composeground.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.example.composeground.viewmodel.TestViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PagingScreen(viewModel: TestViewModel) {
    val listState = rememberLazyListState()
    val itemss = viewModel.items
    val isLoading = viewModel.isLoading

    // 재사용 가능한 페이징 핸들러 호출
    PagingHandler(
        listState = listState,
        itemCount = itemss.size,
        hasNext = viewModel.hasNext,
        isLoading = isLoading,
        onLoadMore = { viewModel.loadItems() }
    )

    LazyColumn(state = listState) {
        items(
            items = itemss,
            key = { item -> item.달러 }
        ) { item ->
            Text(
                text = item.원화,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun PagingHandler(
    listState: LazyListState,
    itemCount: Int,
    hasNext: Boolean,
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= itemCount - 1 &&
                    hasNext &&
                    !isLoading) {
                    onLoadMore()
                }
            }
    }
}

