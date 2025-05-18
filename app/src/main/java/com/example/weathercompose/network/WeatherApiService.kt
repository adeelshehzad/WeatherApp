package com.example.weathercompose.network

import com.example.weathercompose.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json") // The part of the URL after the base URL
    suspend fun getCurrentWeather(@Query("q") postalCode: String, @Query("key") apiKey: String): Response<Weather>
}
