package com.example.weathercompose.model

import android.graphics.Bitmap

data class WeatherData(
    val currentTemperature: String,
    val weatherCondition: String,
    val locationName: String,
    val weatherIcon: Bitmap?
)
