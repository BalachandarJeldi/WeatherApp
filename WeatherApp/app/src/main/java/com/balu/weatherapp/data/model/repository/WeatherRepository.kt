package com.balu.weatherapp.data.model.repository


import android.content.SharedPreferences
import com.balu.weatherapp.data.model.WeatherResponse
import com.balu.weatherapp.data.remote.WeatherApiService
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val weatherApiService: WeatherApiService,
    private val sharedPreferences: SharedPreferences,
) {
    companion object {
        private const val PREF_LAST_CITY = "last_city"
        private const val API_KEY = "1cfa944031f8745554947a9cd609b9ab"
    }

    suspend fun getWeatherByCity(cityName: String): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        try {
            val response = weatherApiService.getWeatherByCity(cityName, API_KEY)

            // Only save if the response is actually valid (OpenWeather uses 200 for success)
            if (response.cod == 200) {
                saveLastCity(cityName)
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isFirstLaunch(): Boolean {
        return !sharedPreferences.contains(PREF_LAST_CITY)
    }

    private fun saveLastCity(cityName: String) {
        sharedPreferences.edit { putString(PREF_LAST_CITY, cityName) }
    }

    fun getLastCity() : String? {
        return sharedPreferences.getString(PREF_LAST_CITY, null)
    }


    suspend fun getWeatherByLocation(latitude: Double, longitude: Double): Result<WeatherResponse>{
        return try {
            val response = weatherApiService.getWeatherByLocation(latitude = latitude, longitude = longitude, apiKey = API_KEY)
            // Only save if the response is actually valid (OpenWeather uses 200 for success)
            if (response.cod == 200) {
                saveLastCity(response.city)
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}