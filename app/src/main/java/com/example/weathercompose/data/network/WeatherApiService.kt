package com.example.weathercompose.network

import com.example.weathercompose.model.Weather
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApiService {
    @GET("current.json") // The part of the URL after the base URL
    suspend fun getCurrentWeather(
        @Query("q") postalCode: String,
        @Query("key") apiKey: String
    ): Response<Weather>

    @GET
    suspend fun getWeatherIcon(@Url iconPath: String): Response<ResponseBody>
}
