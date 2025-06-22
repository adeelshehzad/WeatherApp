package com.example.weathercompose.data.model

data class WeatherData(
    val currentTemperature: String,
    val weatherCondition: String,
    val locationName: String,
    val weatherIcon: String,
    val feelsLike: String,
    val precipitation: String,
    val windSpeed: String,
    val windDirection: String,
    val windDegree: Int,
    val highTemperature: String,
    val lowTemperature: String,
    val sunrise: String,
    val sunset: String,
    val hourlyData: List<HourlyData>,
    val threeDayForecast: List<ForecastData>
)

data class HourlyData(
    val time: String,
    val temperature: String,
    val weatherIcon: String,
    val changeOfRain: Double
)

data class ForecastData(
    val date: String,
    val highTemperature: String,
    val lowTemperature: String,
    val weatherIcon: String,
    val chanceOfRain: Double
)