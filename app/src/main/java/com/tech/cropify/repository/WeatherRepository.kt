package com.tech.cropify.repository

import com.tech.cropify.BuildConfig
import com.tech.cropify.model.weather.WeatherResponse
import com.tech.cropify.network.AgroWeatherApi
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: AgroWeatherApi
) {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): WeatherResponse{
        return weatherApi.getCurrentWeather(
            latitude = latitude,
            longitude = longitude,
            apiKey = BuildConfig.AGRO_API_KEY
        )
    }

    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): List<WeatherResponse> {
        return weatherApi.getForecast(
            latitude = latitude,
            longitude = longitude,
            apiKey = BuildConfig.AGRO_API_KEY
        )
    }
}