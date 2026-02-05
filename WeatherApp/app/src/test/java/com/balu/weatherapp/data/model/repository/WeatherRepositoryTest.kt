package com.balu.weatherapp.data.model.repository

import android.content.SharedPreferences
import com.balu.weatherapp.data.model.WeatherResponse
import com.balu.weatherapp.data.remote.WeatherApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

class WeatherRepositoryTest {
    private val api: WeatherApiService = mockk()
    private val prefs: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private lateinit var repository: WeatherRepository

    @Before
    fun setup() {
        every { prefs.edit() } returns editor
        repository = WeatherRepository(api, prefs)
    }

    // --- City Search Branch Coverage ---

    @Test
    fun `getWeatherByCity - Success - Saves City Name`() = runTest {
        val response = WeatherResponse().apply { cod = 200; city = "London" }
        coEvery { api.getWeatherByCity(any(), any(), any()) } returns response

        repository.getWeatherByCity("London")

        // Verifies the branch where (response.cod == 200)
        verify { editor.putString("last_city", "London") }
    }

    // --- Location Search Branch Coverage ---

    @Test
    fun `getWeatherByLocation - Success - Saves Response City`() = runTest {
        val response = WeatherResponse().apply { cod = 200; city = "Paris" }
        coEvery { api.getWeatherByLocation(any(), any(), any(), any()) } returns response

        val result = repository.getWeatherByLocation(48.85, 2.35)

        assertTrue(result.isSuccess)
        // Verifies saving city name from location response
        verify { editor.putString("last_city", "Paris") }
    }

    @Test
    fun `getWeatherByLocation - Exception - Returns Failure`() = runTest {
        coEvery { api.getWeatherByLocation(any(), any(), any(), any()) } throws Exception("GPS Fail")

        val result = repository.getWeatherByLocation(0.0, 0.0)

        // Verifies the catch block
        assertTrue(result.isFailure)
    }

    // --- SharedPreferences Logic Coverage ---

    @Test
    fun `isFirstLaunch - returns true when no city saved`() {
        every { prefs.contains("last_city") } returns false
        assertTrue(repository.isFirstLaunch()) // Covers true branch
    }

    @Test
    fun `isFirstLaunch - returns false when city exists`() {
        every { prefs.contains("last_city") } returns true
        assertFalse(repository.isFirstLaunch()) // Covers false branch
    }

    @Test
    fun `getLastCity - returns correctly from prefs`() {
        every { prefs.getString("last_city", null) } returns "New York"
        assertEquals("New York", repository.getLastCity())
    }
}