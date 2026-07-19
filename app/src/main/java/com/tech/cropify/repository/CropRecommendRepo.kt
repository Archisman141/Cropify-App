package com.tech.cropify.repository

import android.util.Log
import com.tech.cropify.model.recommend.RecommendationBody
import com.tech.cropify.model.recommend.RecommendationResponse
import com.tech.cropify.network.ApiInterface
import javax.inject.Inject
import javax.inject.Named

class CropRecommendRepo @Inject constructor(
    @Named("backendApiService") private val apiService: ApiInterface
) {
    suspend fun getCropRecommendation(
        body: RecommendationBody
    ): Result<RecommendationResponse>{
        return try {
            val response = apiService.getCropRecommendation(body = body)

            if (response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Crop recommendation failed"))
            }
        } catch (e: Exception){
            Log.e("response","Crop recommendation exception: ${e.message}")
            Result.failure(Exception("Crop recommendation failed"))
        }
    }
}