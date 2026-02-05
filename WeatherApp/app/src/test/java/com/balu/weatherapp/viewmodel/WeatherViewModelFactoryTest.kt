package com.balu.weatherapp.viewmodel

import com.balu.weatherapp.data.model.repository.WeatherRepository
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import androidx.lifecycle.ViewModel

class WeatherViewModelFactoryTest {

    private lateinit var repository: WeatherRepository
    private lateinit var factory: WeatherViewModelFactory

    @Before
    fun setup() {
        // Mock the repository since the Factory only needs the reference
        repository = mockk()
        factory = WeatherViewModelFactory(repository)
    }

    @Test
    fun `create - Valid ViewModel class - Returns WeatherViewModel`() {
        // When: We request a WeatherViewModel from the factory
        val viewModel = factory.create(WeatherViewModel::class.java)

        // Then: The result should not be null and should be an instance of WeatherViewModel
        assertNotNull(viewModel)
        assertTrue(viewModel is WeatherViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create - Invalid ViewModel class - Throws IllegalArgumentException`() {
        // Given: A dummy ViewModel class that the factory doesn't support
        class UnknownViewModel : ViewModel()

        // When: We request the unsupported ViewModel
        factory.create(UnknownViewModel::class.java)

        // Then: The @Test(expected = ...) catches the IllegalArgumentException
    }
}