package com.tech.cropify.api

import retrofit2.Response
import retrofit2.http.Body
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
}