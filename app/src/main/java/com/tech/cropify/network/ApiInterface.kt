package com.tech.cropify.network

import com.tech.cropify.model.hybrid.HybridAdvisoryBody
import com.tech.cropify.model.hybrid.HybridAdvisoryResponse
import com.tech.cropify.model.login.AuthRequest
import com.tech.cropify.model.login.GoogleLoginRequest
import com.tech.cropify.model.login.LoginBody
import com.tech.cropify.model.login.LoginResponse
import com.tech.cropify.model.login.RegisterBody
import com.tech.cropify.model.login.RegisterResponse
import com.tech.cropify.model.prediction.Prediction
import com.tech.cropify.model.prediction.PredictionBody
import com.tech.cropify.model.recommend.RecommendationBody
import com.tech.cropify.model.recommend.RecommendationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginBody
    ): Response<LoginResponse>

//    @POST("auth/user-details")
//    suspend fun getUserDetails(
//        @Body body: AuthRequest
//    ): Response<RegisterResponse>

    @Multipart
    @POST("auth/user-details")
    suspend fun postUserDetails(
        @Part("data") data: RequestBody,
        @Part profilePic: MultipartBody.Part?,
        @Header("dauth") token: String
    ): String

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

    @GET("auth/user-details")
    suspend fun getUserDetails(
        @Header("dauth") token : String
    ): Response<LoginResponse>

    @POST("ml/recommendation")
    suspend fun getCropRecommendation(
        @Body body: RecommendationBody
    ): Response<RecommendationResponse>

    @POST("ml/hybridadvisory")
    suspend fun getHybridAdvisory(
        @Body body: HybridAdvisoryBody
    ): Response<HybridAdvisoryResponse>

//    @POST("ml/disease")
//    suspend fun mlPredictDisease(): Response<>
}