package com.tech.cropify.repository

import android.util.Log
import com.tech.cropify.model.login.LoginBody
import com.tech.cropify.model.prediction.Prediction
import com.tech.cropify.model.prediction.PredictionBody
import com.tech.cropify.network.ApiInterface
import javax.inject.Inject
import javax.inject.Named

class MLPrediction @Inject constructor(
    @Named("backendApiService") val api: ApiInterface
) {
    suspend fun prediction(
        body: PredictionBody
    ): Result<Prediction>{
        return try {
            val response = api.mlPrediction(
                body = body
            )

            if (response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Log.e("response","Prediction error: ${response.errorBody()}")
                Result.failure(Exception("Prediction failed"))
            }
        }catch (e: Exception){
            Log.e("response","Login exception: ${e.message}")
            Result.failure(Exception("Login failed"))
        }
    }
}