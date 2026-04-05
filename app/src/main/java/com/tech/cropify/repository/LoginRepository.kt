package com.tech.cropify.repository

import android.util.Log
import com.tech.cropify.api.ApiInterface
import com.tech.cropify.api.LoginBody
import com.tech.cropify.api.LoginResponse
import com.tech.cropify.api.RegisterBody
import com.tech.cropify.api.RegisterResponse
import javax.inject.Inject
import javax.inject.Named


class LoginRepository @Inject constructor(@Named("backendApiService") val api: ApiInterface){
    suspend fun login(emailId: String, password: String): Result<LoginResponse>{
        return try {
            val response = api.login(
                LoginBody(
                    emailId = emailId,
                    password = password
                )
            )

            if (response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Log.e("response","Login error: ${response.errorBody()}")
                Result.failure(Exception("Login failed"))
            }
        }catch (e: Exception){
            Log.e("response","Login exception: ${e.message}")
            Result.failure(Exception("Login failed"))
        }
    }

    suspend fun register(username: String, password: String): Result<RegisterResponse>{
        return try {
            val response = api.register(
                RegisterBody(
                    userName = username,
                    password = password
                )
            )

            if (response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Log.e("response","Login error: ${response.errorBody()}")
                Result.failure(Exception("Login failed"))
            }
        }catch (e: Exception){
            Log.e("response","Login exception: ${e.message}")
            Result.failure(Exception("Login failed"))
        }
    }
}