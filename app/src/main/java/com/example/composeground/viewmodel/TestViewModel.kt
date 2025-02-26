package com.example.composeground.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {

    var items by mutableStateOf(listOf<TestData>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var page by mutableStateOf(1)
        private set

    var totalCount by mutableStateOf(0)
        private set

    var hasNext by mutableStateOf(true)
        private set

    init {
        loadItems() // 첫 페이지 데이터 로딩
    }

    fun loadItems() {
        // 이미 로딩 중이거나 더 이상 페이지가 없으면 종료
        if (isLoading || !hasNext) return

        isLoading = true
        viewModelScope.launch {
//            // Repository를 통해 페이지 데이터 요청 (페이지당 20개)
//            val response = Repository.getItems(page, 20)
//            // 기존 아이템 목록에 새 데이터를 추가
//            items = items + response.list
//            totalCount = response.totalCount
//            hasNext = response.hasNext
//            page++  // 다음 페이지 번호 업데이트
//            isLoading = false
        }
    }

}

/*
object Repository {
    // suspend 함수를 이용해 네트워크 호출(또는 더미 데이터 생성)을 시뮬레이션
    suspend fun getItems(page: Int, pageSize: Int): ApiResponse<TestData> {
        // 시작 인덱스 계산
        val start = (page - 1) * pageSize
        // 더미 아이템 목록 생성
        val list = (start until start + pageSize).map { Item(it, "Item $it") }
        val totalCount = 100  // 예를 들어 전체 아이템 수가 100개라고 가정
        val hasNext = (page * pageSize) < totalCount
        delay(1000) // 네트워크 딜레이 시뮬레이션
        return ApiResponse(list, totalCount, hasNext)
    }
}
*/

data class TestData(
    val 달러: String,
    val 원화: String
)