package com.example.weathercompose.data.model

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
            precipitation = "",
            windSpeed = "",
            windDirection = "",
            windDegree = 0,
            highTemperature = highTemperature,
            lowTemperature = lowTemperature,
            sunrise = "",
            sunset = "",
            hourlyData = emptyList(),
            threeDayForecast = emptyList()
        )
    }
}
