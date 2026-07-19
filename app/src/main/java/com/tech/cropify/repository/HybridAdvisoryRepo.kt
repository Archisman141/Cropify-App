package com.tech.cropify.repository

import android.util.Log
import com.tech.cropify.model.hybrid.HybridAdvisoryBody
import com.tech.cropify.model.hybrid.HybridAdvisoryResponse
import com.tech.cropify.network.ApiInterface
import javax.inject.Inject
import javax.inject.Named

class HybridAdvisoryRepo @Inject constructor(
    @Named("backendApiService") private val apiService: ApiInterface
) {
    suspend fun getHybridAdvisory(body: HybridAdvisoryBody): Result<HybridAdvisoryResponse> {
        return try {
            val response = apiService.getHybridAdvisory(body = body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Hybrid advisory failed"))
            }
        } catch (e: Exception) {
            Log.e("response", "Hybrid advisory exception: ${e.message}")
            Result.failure(Exception("Hybrid advisory failed"))
        }
    }
}