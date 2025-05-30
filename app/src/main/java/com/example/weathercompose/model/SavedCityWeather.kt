package com.example.weathercompose.model

data class SavedCityWeather(
    val cityName: String,
    val currentTemperature: String,
    val weatherCondition: String,
    val weatherIcon: String,
    val feelsLike: String = "",
    val highTemperature: String = "",
    val lowTemperature: String = ""
) {
    fun toWeatherData(): WeatherData {
        return WeatherData(
            locationName = cityName,
            currentTemperature = currentTemperature,
            weatherCondition = weatherCondition,
            weatherIcon = weatherIcon,
            feelsLike = feelsLike,
            highTemperature = highTemperature,
            lowTemperature = lowTemperature,
            hourlyData = emptyList()
        )
    }
}
