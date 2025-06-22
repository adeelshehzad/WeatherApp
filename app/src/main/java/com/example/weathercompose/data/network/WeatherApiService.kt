package com.example.weathercompose.data.network

import com.example.weathercompose.data.network.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("key") apiKey: String
    ): Response<WeatherResponse>
}
