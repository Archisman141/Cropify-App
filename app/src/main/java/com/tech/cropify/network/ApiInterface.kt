package com.tech.cropify.network

import com.tech.cropify.model.LoginBody
import com.tech.cropify.model.LoginResponse
import com.tech.cropify.model.RegisterBody
import com.tech.cropify.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterBody
    ): Response<RegisterResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("dauth") token: String): Response<String>
}