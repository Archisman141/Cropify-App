package com.tech.cropify.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.recommend.RecommendationBody
import com.tech.cropify.model.recommend.RecommendationResponse
import com.tech.cropify.repository.CropRecommendRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendUiState(
    val isLoading: Boolean = false,
    val result: RecommendationResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CropRecommendViewModel @Inject constructor(
    private val repository: CropRecommendRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendUiState())
    val uiState = _uiState.asStateFlow()

    fun getRecommendation(body: RecommendationBody) {
        viewModelScope.launch {
            _uiState.value = RecommendUiState(isLoading = true)
            repository.getCropRecommendation(body)
                .onSuccess { _uiState.value = RecommendUiState(result = it) }
                .onFailure { _uiState.value = RecommendUiState(error = it.message ?: "Something went wrong") }
        }
    }

    fun clearResult() {
        _uiState.value = RecommendUiState()
    }
}