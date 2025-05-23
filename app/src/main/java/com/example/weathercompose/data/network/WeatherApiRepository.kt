package com.example.weathercompose.data.network

import com.example.weathercompose.BuildConfig
import com.example.weathercompose.data.local.City
import com.example.weathercompose.data.local.CityDatabase
import com.example.weathercompose.model.Weather

interface WeatherApiRepository {
    suspend fun getCurrentWeather(postalCode: String): Weather
    suspend fun getSavedCities(): List<City>
    suspend fun saveCity(city: City)
}

class WeatherApiRepositoryImpl(
    private val apiService: WeatherApiService,
    private val cityDatabase: CityDatabase
) : WeatherApiRepository {
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

    override suspend fun getSavedCities(): List<City> {
        return cityDatabase.cityDao().getAllCities()
    }

    override suspend fun saveCity(city: City) {
        cityDatabase.cityDao().insertCity(city)
    }
}