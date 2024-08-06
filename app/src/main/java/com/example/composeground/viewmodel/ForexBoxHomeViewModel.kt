package com.example.composeground.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForexBoxHomeViewModel : ViewModel() {

    private val _uiState = MutableLiveData<ForexBoxHomeUiState>()
    val uiState: MutableLiveData<ForexBoxHomeUiState> get() = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.value = ForexBoxHomeUiState.Loading
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            _uiState.value = ForexBoxHomeUiState.Error
        }) {
            delay(3000)
            _uiState.value = ForexBoxHomeUiState.Success(
                Data(
                    달러 = "55",
                    원화 = "75,782",
                )
            )
        }
    }
}

sealed interface ForexBoxHomeUiState {
    data class Success(val data: Data) : ForexBoxHomeUiState
    data object Error : ForexBoxHomeUiState
    data object Loading : ForexBoxHomeUiState
}

data class Data(
    val 달러: String,
    val 원화: String
)