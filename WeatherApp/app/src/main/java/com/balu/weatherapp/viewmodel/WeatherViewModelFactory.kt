package com.balu.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.balu.weatherapp.data.model.repository.WeatherRepository

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Defensive check: Ensure we are only creating the WeatherViewModel
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }
        // If this factory is called for a different ViewModel, throw an error to prevent crashes
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}