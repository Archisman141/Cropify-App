package com.tech.cropify.model.weather

data class WeatherResponse(
    val dt: Long,
    val weather: List<WeatherInfo>,
    val main: MainWeather,
    val wind: Wind,
    val clouds: Clouds
)

data class WeatherInfo(
    val id: String,
    val main: String,
    val description: String,
    val icon: String
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Clouds(
    val all: Int
)

/** A per-day summary aggregated from the 3-hour forecast entries. */
data class DailyForecast(
    val label: String,        // e.g. "Today", "Mon"
    val icon: String,         // OpenWeather icon code, e.g. "10d"
    val description: String,  // representative condition text
    val tempC: Int,           // representative (midday) temperature, in °C
    val highC: Int,           // highest temperature of the day, in °C
    val lowC: Int,            // lowest temperature of the day, in °C
    val humidity: Int,        // %
    val windKmh: Int,         // km/h
    val clouds: Int,          // %
    val pressure: Int         // hPa
)
