package com.example.weathercompose.compose.model

data class Forecast(
    val day: String,
    val temperature: String,
    val weatherIcon: Int,
    val weatherCondition: String
)
