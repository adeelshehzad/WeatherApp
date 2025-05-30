package com.example.weathercompose.model

data class WeatherData(
    val currentTemperature: String,
    val weatherCondition: String,
    val locationName: String,
    val weatherIcon: String,
    val feelsLike: String,
    val highTemperature: String,
    val lowTemperature: String
)
