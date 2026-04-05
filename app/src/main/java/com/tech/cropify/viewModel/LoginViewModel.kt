package com.tech.cropify.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
): ViewModel() {

    companion object{
        const val TAG = "LoginViewModel"
    }

    fun login(emailId: String, password: String){
        viewModelScope.launch {
            try {
                val response = repository.login(emailId, password)
                response.fold(
                    onSuccess = {
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

    fun register(username: String, password: String){
        viewModelScope.launch {
            try {
                val response = repository.register(username, password)
                response.fold(
                    onSuccess = {
                        Log.d(TAG,"Success: ${it.message}")
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
}