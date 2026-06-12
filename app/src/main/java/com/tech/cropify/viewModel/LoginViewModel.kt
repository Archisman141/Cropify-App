package com.tech.cropify.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.LoginResponse
import com.tech.cropify.repository.LoginRepository
import com.tech.cropify.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
): ViewModel() {

    companion object{
        const val TAG = "LoginViewModel"
    }

    private var _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(emailId: String, password: String, context: Context){
        viewModelScope.launch {
            try {
                val response = repository.login(emailId, password)
                _authState.value = AuthState.Loading
                response.fold(
                    onSuccess = { response ->
                        _token.value = response.token
                        SharedPreferenceManager.saveToken(context = context, token = response.token)
                        StateHolder.accessToken?.text = response.token
                        AuthState.LoginSuccess
                        Log.d(TAG,"Success access token: ${response.token}")
                    },
                    onFailure = {
                        AuthState.Error(it.message ?: "Unknown error")
                        Log.d(TAG,"Failure: ${it.message}")
                    }
                )
            }catch (e: Exception){
                Log.d(TAG,"Exception: ${e.message}")
            }

        }
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()


    fun loginWithGoogle(idToken: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.loginWithGoogle(idToken)
                .onSuccess { response ->
                    _token.value = response.token
                    SharedPreferenceManager.saveToken(response.token, context)
                    StateHolder.accessToken = State(text = response.token)
                    _uiState.value = AuthUiState.Success(response)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(username, password)
                response.fold(
                    onSuccess = {
                        Log.d(TAG, "Success: ${it.message}")
                    },
                    onFailure = {
                        Log.d(TAG, "Failure: ${it.message}")
                    }
                )
            } catch (e: Exception) {
                Log.d(TAG, "Exception: ${e.message}")
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.logout(context)
            _authState.value = result.fold(
                onSuccess = { AuthState.LoggedOut },
                onFailure = { AuthState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object LoginSuccess : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class AuthUiState{
    object Idle: AuthUiState()
    object Loading: AuthUiState()
    data class Success(val response: LoginResponse): AuthUiState()
    data class Error(val message: String): AuthUiState()
}


object StateHolder {

    private const val TAG = "CallStateHolder"


    @Volatile
    var accessToken: State ?= null

}

data class State(
    var text: String? = null,
)