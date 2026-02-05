package com.balu.weatherapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Note: Centralized Retrofit configuration.
 * Using 'lazy' ensures the service is only initialized when needed,
 * saving memory on app startup.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}