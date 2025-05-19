package com.example.weathercompose.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    const val BASE_URL = "https://api.weatherapi.com/v1/"

    // Create a logging interceptor (optional)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }

    // Create an OkHttpClient and add the logging interceptor
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add this line for logging
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient) // Set the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // For JSON parsing
            .build()
    }

    val api: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}