package com.example.weathercompose.data.network

import com.example.weathercompose.BuildConfig
import com.example.weathercompose.data.local.City
import com.example.weathercompose.data.local.CityDatabase
import com.example.weathercompose.model.WeatherResponse

interface WeatherApiRepository {
    suspend fun getWeather(location: String, days: Int): WeatherResponse
    suspend fun getSavedCities(): List<City>
    suspend fun saveCity(city: City)
}

class WeatherApiRepositoryImpl(
    private val apiService: WeatherApiService,
    private val cityDatabase: CityDatabase
) : WeatherApiRepository {
    override suspend fun getWeather(location: String, days: Int): WeatherResponse {
        return try {
            val response = apiService.getWeather(location, days, BuildConfig.WEATHER_API_KEY)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API request failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSavedCities(): List<City> {
        return cityDatabase.cityDao().getAllCities()
    }

    override suspend fun saveCity(city: City) {
        cityDatabase.cityDao().insertCity(city)
    }
}