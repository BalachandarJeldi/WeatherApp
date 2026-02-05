package com.balu.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balu.weatherapp.data.model.repository.WeatherRepository
import com.balu.weatherapp.viewstate.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel ( private val repository: WeatherRepository) : ViewModel() {

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState

    fun onAppLaunch(latitude: Double?, longitude: Double?){
        val lastCity = repository.getLastCity()
        if (!lastCity.isNullOrBlank()) {
            getWeatherByCity(cityName = lastCity)
        } else if(latitude != null && longitude != null){
            // [cite: 32] Priority: Current Location
            getWeatherByLocation(latitude = latitude, longitude = longitude)
        } else {
            //  Fallback: Last City
            loadLastSearchedCity()
        }
    }

    /**
     * Logic to fulfill: "Auto-load the last city searched upon app launch"
     * This safely bridges the Repository to the UI State.
     */
    fun loadLastSearchedCity() {
        val lastCity = repository.getLastCity()
        if (!lastCity.isNullOrBlank()) {
            getWeatherByCity(lastCity)
        } else {
            // If no city exists in cache, keep state as Empty
            _weatherUiState.value = WeatherUiState.Empty
        }
    }

    fun getWeatherByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            repository.getWeatherByLocation(latitude, longitude)
                .onSuccess { weatherResponse ->
                    _weatherUiState.value = WeatherUiState.Success(weatherResponse)
                }
                .onFailure { exception ->
                    _weatherUiState.value = WeatherUiState.Error(exception.localizedMessage ?: "Unknown error")
                }
        }
    }

    fun getWeatherByCity(cityName: String) {
        if(cityName.isBlank()) return
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            repository.getWeatherByCity(cityName)
                .onSuccess { weatherResponse ->
                    _weatherUiState.value = WeatherUiState.Success(weatherResponse)
                }
                .onFailure { exception ->
                    _weatherUiState.value = WeatherUiState.Error(exception.localizedMessage ?: "Unknown error")
                }
        }
    }



}