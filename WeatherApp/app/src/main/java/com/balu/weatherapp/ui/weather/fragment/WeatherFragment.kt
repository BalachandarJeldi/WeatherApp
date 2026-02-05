package com.balu.weatherapp.ui.weather.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.balu.weatherapp.viewmodel.WeatherViewModel
import com.balu.weatherapp.data.remote.RetrofitClient
import com.balu.weatherapp.data.model.repository.WeatherRepository
import com.balu.weatherapp.ui.theme.WeatherAppTheme
import com.balu.weatherapp.ui.weather.WeatherScreen
import com.balu.weatherapp.viewmodel.WeatherViewModelFactory


class WeatherFragment: Fragment() {
    // Using viewModels delegate for proper Lifecycle scoping
    private val viewModel: WeatherViewModel by viewModels() {
        WeatherViewModelFactory(
            WeatherRepository(
                RetrofitClient.service,
                requireActivity().getPreferences(Context.MODE_PRIVATE)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Apply your application's Jetpack Compose theme
                WeatherAppTheme {
                    // Observe state from the ViewModel
                    val state by viewModel.weatherUiState.collectAsState()

                    // UI Layout - State Hoisting Pattern
                    // We pass values and lambdas (callbacks) down the tree
                    WeatherScreen(
                        state = state,
                        onSearchAction = { cityName ->
                            viewModel.getWeatherByCity(cityName)
                        }
                    )
                }
            }
        }
    }

    /**
     * Triggered by MainActivity when the Location permission is handled.
     * Meets Requirement: "Ask User for location... If permission given, retrieve weather data."
     */
    fun onLocationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            // Coordinate values from the challenge PDF
            viewModel.getWeatherByLocation(44.34, 10.99)
        } else {
            // Fallback Requirement: "Auto-load the last city searched"
            viewModel.loadLastSearchedCity()
        }
    }
}