package com.example.weathercompose.network

import com.example.weathercompose.model.Weather
import com.example.weathercompose.BuildConfig

interface WeatherApiRepository {
    suspend fun getCurrentWeather(postalCode: String): Weather
}

class WeatherApiRepositoryImpl(private val apiService: WeatherApiService) : WeatherApiRepository {
    override suspend fun getCurrentWeather(postalCode: String): Weather {
        return try {
            val response = apiService.getCurrentWeather(postalCode, BuildConfig.WEATHER_API_KEY)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API request failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}