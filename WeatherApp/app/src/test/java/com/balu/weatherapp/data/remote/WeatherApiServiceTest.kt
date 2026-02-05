package com.balu.weatherapp.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var api: WeatherApiService

    @Before
    fun setup() {
        server = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    @Test
    fun `getWeatherByCity - Success - Parses Full JSON`() = runBlocking {
        val json = """{
            "name": "Reston", 
            "cod": 200, 
            "main": {"temp": 55.5, "humidity": 40.0},
            "weather": [{"description": "clear sky"}]
        }"""
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = api.getWeatherByCity("Reston", "key")

        assertEquals("Reston", result.city)
        assertEquals(55.5, result.main.temp, 0.1)
        assertEquals("clear sky", result.weather[0].description)
    }

    @Test
    fun `getWeatherByLocation - Success - Correct URL Query`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"cod": 200}"""))

        api.getWeatherByLocation(10.0, 20.0, "key")

        val request = server.takeRequest()
        // Verify query parameters are correctly appended
        assertTrue(request.path!!.contains("lat=10.0"))
        assertTrue(request.path!!.contains("lon=20.0"))
        assertTrue(request.path!!.contains("units=imperial"))
    }

    @After
    fun tearDown() = server.shutdown()
}