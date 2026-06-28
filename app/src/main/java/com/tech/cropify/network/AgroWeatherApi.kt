package com.tech.cropify.network

import com.tech.cropify.model.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AgroWeatherApi {

    @GET("agro/1.0/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): WeatherResponse

    /**
     * Weather forecast in 3-hour steps (~5–7 days). The agromonitoring endpoint
     * returns a bare JSON array of entries shaped like [WeatherResponse].
     */
    @GET("agro/1.0/weather/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): List<WeatherResponse>
}