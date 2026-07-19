package com.tech.cropify.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.hybrid.HybridAdvisoryBody
import com.tech.cropify.model.hybrid.HybridAdvisoryResponse
import com.tech.cropify.repository.HybridAdvisoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HybridUiState(
    val isLoading: Boolean = false,
    val result: HybridAdvisoryResponse? = null,
    val error: String? = null
)

@HiltViewModel
class HybridAdvisoryViewModel @Inject constructor(
    private val repository: HybridAdvisoryRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(HybridUiState())
    val uiState = _uiState.asStateFlow()

    fun getHybridAdvice(body: HybridAdvisoryBody) {
        viewModelScope.launch {
            _uiState.value = HybridUiState(isLoading = true)
            repository.getHybridAdvisory(body)
                .onSuccess { _uiState.value = HybridUiState(result = it) }
                .onFailure { _uiState.value = HybridUiState(error = it.message ?: "Something went wrong") }
        }
    }

    fun clearResult() {
        _uiState.value = HybridUiState()
    }
}