package com.tech.cropify.network

import com.tech.cropify.model.login.AuthRequest
import com.tech.cropify.model.login.GoogleLoginRequest
import com.tech.cropify.model.login.LoginBody
import com.tech.cropify.model.login.LoginResponse
import com.tech.cropify.model.login.RegisterBody
import com.tech.cropify.model.login.RegisterResponse
import com.tech.cropify.model.prediction.Prediction
import com.tech.cropify.model.prediction.PredictionBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): Response<LoginResponse>

//    @POST("auth/user-details")
//    suspend fun getUserDetails(
//        @Body body: AuthRequest
//    ): Response<RegisterResponse>

    @POST("auth/user-details")
    suspend fun postUserDetails(@Body request: AuthRequest): String

    @POST("auth/google-login")
    suspend fun loginWithGoogle(
        @Body body: GoogleLoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterBody
    ): Response<RegisterResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("dauth") token: String): Response<String>

    @POST("ml/predict")
    suspend fun mlPrediction(
        @Body body: PredictionBody
    ): Response<Prediction>

//    @POST("ml/disease")
//    suspend fun mlPredictDisease(): Response<>
}