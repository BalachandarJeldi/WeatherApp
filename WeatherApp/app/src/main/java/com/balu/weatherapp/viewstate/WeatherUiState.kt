package com.balu.weatherapp.viewstate


import com.balu.weatherapp.data.model.WeatherResponse
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    object Empty : WeatherUiState()
}