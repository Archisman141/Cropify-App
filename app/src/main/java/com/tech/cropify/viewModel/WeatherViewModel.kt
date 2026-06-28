package com.tech.cropify.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tech.cropify.model.weather.DailyForecast
import com.tech.cropify.model.weather.WeatherResponse
import com.tech.cropify.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    companion object {
        const val TAG = "WeatherViewModel"
        private const val FORECAST_DAYS = 7
    }

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private val _forecast = MutableStateFlow<List<DailyForecast>>(emptyList())
    val forecast = _forecast.asStateFlow()

    fun getWeather(
        latitute: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            try {
                _weather.value = weatherRepository.getWeather(latitute, longitude)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val entries = weatherRepository.getForecast(latitute, longitude)
                _forecast.value = aggregateDaily(entries)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Collapse the 3-hour forecast entries into one summary per day (up to a week). */
    private fun aggregateDaily(entries: List<WeatherResponse>): List<DailyForecast> {
        if (entries.isEmpty()) return emptyList()

        // Group entries by calendar day, preserving chronological order.
        val byDay = LinkedHashMap<Long, MutableList<WeatherResponse>>()
        for (e in entries) {
            val cal = Calendar.getInstance().apply { timeInMillis = e.dt * 1000L }
            val key = cal.get(Calendar.YEAR) * 1000L + cal.get(Calendar.DAY_OF_YEAR)
            byDay.getOrPut(key) { mutableListOf() }.add(e)
        }

        val today = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        return byDay.values.take(FORECAST_DAYS).map { dayEntries ->
            val temps = dayEntries.map { it.main.temp }
            val high = (temps.max() - 273.15).roundToInt()
            val low = (temps.min() - 273.15).roundToInt()

            // Pick the entry closest to midday as representative for the whole day.
            val midday = dayEntries.minByOrNull { e ->
                val cal = Calendar.getInstance().apply { timeInMillis = e.dt * 1000L }
                abs(cal.get(Calendar.HOUR_OF_DAY) - 12)
            } ?: dayEntries.first()
            val condition = midday.weather.firstOrNull()

            val cal = Calendar.getInstance().apply { timeInMillis = dayEntries.first().dt * 1000L }
            val label = if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
            ) "Today" else dayFormat.format(cal.time)

            DailyForecast(
                label = label,
                icon = condition?.icon ?: "01d",
                description = condition?.description.orEmpty(),
                tempC = (midday.main.temp - 273.15).roundToInt(),
                highC = high,
                lowC = low,
                humidity = midday.main.humidity,
                windKmh = (midday.wind.speed * 3.6).roundToInt(),
                clouds = midday.clouds.all,
                pressure = midday.main.pressure
            )
        }
    }
}
