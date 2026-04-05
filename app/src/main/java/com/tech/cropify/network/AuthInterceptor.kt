package com.tech.cropify.network

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.tech.cropify.network.AppEnv.D_AUTH
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context): Interceptor {
    @SuppressLint("LogNotTimber")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()


        requestBuilder.addHeader(
            "dauth",
            //  "android@1212"
            "$D_AUTH"
            //"7777777"
        )

        Log.d("AuthInterceptor", "Adding dauth header to ${originalRequest.url}")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }

}