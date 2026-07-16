package com.tech.cropify.repository

import android.content.Context
import android.util.Log
import com.tech.cropify.model.login.AuthRequest
import com.tech.cropify.model.login.GoogleLoginRequest
import com.tech.cropify.network.ApiInterface
import com.tech.cropify.model.login.LoginBody
import com.tech.cropify.model.login.LoginResponse
import com.tech.cropify.model.login.RegisterBody
import com.tech.cropify.model.login.RegisterResponse
import com.tech.cropify.util.SharedPreferenceManager
import javax.inject.Inject
import javax.inject.Named

class LoginRepository @Inject constructor(
    @Named("backendApiService") val api: ApiInterface
){
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

    suspend fun loginWithGoogle(idToken: String): Result<LoginResponse> {
        return try {
            val response = api.loginWithGoogle(
                GoogleLoginRequest(idtoken = idToken)
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Log.e("AuthRepository", "Login error: ${response.errorBody()?.string()}")
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception: ${e.message}")
            Result.failure(Exception("Login failed: ${e.message}"))
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

    suspend fun logout(context: Context): Result<String> {
        return try {
            val token = SharedPreferenceManager.getToken(context = context)
                ?: return Result.failure(Exception("No token found"))

            val response = api.logout(token)

            SharedPreferenceManager.clearToken(context)

            if (response.isSuccessful) {
                Result.success(response.body() ?: "Logged out")
            } else {
                Result.failure(Exception("Logout failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            SharedPreferenceManager.clearToken(context)
            Result.failure(e)
        }
    }
}