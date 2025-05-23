package com.example.weathercompose.model

data class SavedCityWeather(
    val cityName: String,
    val currentTemperature: String,
    val weatherCondition: String,
    val weatherIcon: String
) {
    fun toWeatherData(): WeatherData {
        return WeatherData(
            locationName = cityName,
            currentTemperature = currentTemperature,
            weatherCondition = weatherCondition,
            weatherIcon = weatherIcon
        )
    }
}
