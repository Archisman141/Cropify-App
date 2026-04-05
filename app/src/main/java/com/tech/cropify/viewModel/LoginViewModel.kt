package com.tech.cropify.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.repository.LoginRepository
import com.tech.cropify.util.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun login(emailId: String, password: String, context: Context){
        viewModelScope.launch {
            try {
                val response = repository.login(emailId, password)
                response.fold(
                    onSuccess = {
                        _token.value = it.token
                        SharedPreferenceManager.saveToken(context = context, token = it.token)
                        StateHolder.accessToken?.text = it.token
                        Log.d(TAG,"Success access token: ${it.token}")
                    },
                    onFailure = {
                        Log.d(TAG,"Failure: ${it.message}")
                    }
                )
            }catch (e: Exception){
                Log.d(TAG,"Exception: ${e.message}")
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
}


object StateHolder {

    private const val TAG = "CallStateHolder"


    @Volatile
    var accessToken: State ?= null

}

data class State(
    var text: String? = null,
)