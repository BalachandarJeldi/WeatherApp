package com.balu.weatherapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.balu.weatherapp.data.model.WeatherResponse
import com.balu.weatherapp.data.model.repository.WeatherRepository
import com.balu.weatherapp.viewstate.WeatherUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: WeatherRepository = mockk()
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAppLaunch - Scenario 1 - Saved City Exists`() = runTest {
        coEvery { repository.getLastCity() } returns "Reston"
        coEvery { repository.getWeatherByCity("Reston") } returns Result.success(createMockResponse("Reston"))

        viewModel.onAppLaunch(44.34, 10.99) // Even if coords provided

        val state = viewModel.weatherUiState.value as WeatherUiState.Success
        assertEquals("Reston", state.data.city)
    }

    @Test
    fun `onAppLaunch - Scenario 2 - First Launch (No City) with Coords`() = runTest {
        coEvery { repository.getLastCity() } returns null
        coEvery { repository.getWeatherByLocation(any(), any()) } returns Result.success(createMockResponse("Zocca"))

        viewModel.onAppLaunch(44.34, 10.99)

        val state = viewModel.weatherUiState.value as WeatherUiState.Success
        assertEquals("Zocca", state.data.city)
    }

    @Test
    fun `getWeatherByCity - Scenario 3 - API Error Handling`() = runTest {
        val errorMessage = "HTTP 404 Not Found"
        coEvery { repository.getWeatherByCity(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.getWeatherByCity("InvalidCity")

        val state = viewModel.weatherUiState.value as WeatherUiState.Error
        assertEquals(errorMessage, state.message)
    }

    @Test
    fun `getWeatherByCity - Scenario 4 - Empty Input Guard`() = runTest {
        viewModel.getWeatherByCity("")
        // Verify state remains Empty/Initial and no repo call was made
        assert(viewModel.weatherUiState.value is WeatherUiState.Empty)
        coVerify(exactly = 0) { repository.getWeatherByCity(any()) }
    }

    private fun createMockResponse(name: String) = WeatherResponse().apply {
        city = name
        cod = 200
        main = WeatherResponse.Main().apply { temp = 72.0 }
    }
}