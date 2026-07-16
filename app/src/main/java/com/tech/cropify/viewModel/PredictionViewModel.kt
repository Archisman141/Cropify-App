//package com.tech.cropify.viewModel
//
//import android.util.Log
//import androidx.compose.runtime.MutableState
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.tech.cropify.model.prediction.Prediction
//import com.tech.cropify.repository.MLPrediction
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//
//@HiltViewModel
//class PredictionViewModel @Inject constructor(
//    val repository: MLPrediction
//): ViewModel() {
//
//    private val _aiAnswerState = MutableStateFlow(Prediction())
//    val aiAnswerState: StateFlow<AIAnswerState> = _aiAnswerState.asStateFlow()
//
//
//    fun getPrediction(){
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//
//                _aiAnswerState.value =
//                    _aiAnswerState.value.copy(isLoading = true)
//
//
//                _aiAnswerState.value =
//                    _aiAnswerState.value.copy(
//                        shortAnswer = shortAnswer,
//                        detailedAnswer = detailedAnswer,
//                        questionType = questionType,
//                        isLoading = false,
//                        isDetailed = false
//                    )
//
//
//            } catch (e: Exception) {
//                Log.e("ClaudeAI_VM", "Unified fetch error", e)
//                _aiAnswerState.value =
//                    _aiAnswerState.value.copy(isLoading = false)
//            }
//        }
//    }
//}


package com.tech.cropify.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.prediction.Prediction
import com.tech.cropify.model.prediction.PredictionBody
import com.tech.cropify.repository.MLPrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PredictionUiState(
    val isLoading: Boolean = false,
    val result: Prediction? = null,
    val error: String? = null
)

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val repository: MLPrediction
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun getPrediction(body: PredictionBody) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.prediction(body)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(isLoading = false, result = result)
                }
                .onFailure { e ->
                    Log.e("PredictionVM", "Prediction fetch error", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Something went wrong. Please try again."
                    )
                }
        }
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(result = null, error = null)
    }
}