package com.example.weathercompose.model

data class WeatherData(
    val currentTemperature: String,
    val weatherCondition: String,
    val locationName: String,
    val weatherIcon: String
)
