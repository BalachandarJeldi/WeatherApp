package com.balu.weatherapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.balu.weatherapp.data.model.repository.WeatherRepository
import com.balu.weatherapp.data.remote.RetrofitClient
import com.balu.weatherapp.ui.theme.WeatherAppTheme
import com.balu.weatherapp.ui.weather.WeatherScreen
import com.balu.weatherapp.viewmodel.WeatherViewModel
import com.balu.weatherapp.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    // ViewModel initialized via Factory directly in the Activity
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            WeatherRepository(
                RetrofitClient.service,
                getPreferences(Context.MODE_PRIVATE)
            )
        )
    }

    // Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        // Priority: Current Location if granted, else Fallback
        if (granted) {
            viewModel.onAppLaunch(44.34, 10.99)
        } else {
            viewModel.onAppLaunch(null, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Trigger permission check immediately
        requestPermissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )

        setContent {
            WeatherAppTheme {
                // We call the WeatherScreen directly from the Activity
                WeatherScreen(
                    state = viewModel.weatherUiState.collectAsState().value,
                    onSearchAction = { cityName ->
                        viewModel.getWeatherByCity(cityName)
                    }
                )
            }
        }
    }
}