package com.example.weathercompose.model

data class AirQuality(
    val co: Double,
    val gbDefraIndex: Int,
    val no2: Double,
    val o3: Double,
    val pm10: Int,
    val pm2_5: Double,
    val so2: Int,
    val usEpaIndex: Int
)