package com.example.weathercompose.model


import com.example.weathercompose.formatHourTime
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current")
    val current: Current,
    @SerializedName("forecast")
    val forecast: Forecast,
    @SerializedName("location")
    val location: Location
)

fun WeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        currentTemperature = current.tempC.toString(),
        weatherCondition = current.condition.text,
        locationName = location.name,
        weatherIcon = current.condition.icon,
        feelsLike = current.feelslikeC.toString(),
        highTemperature = forecast.forecastDay[0].day.maxtempC.toString(),
        lowTemperature = forecast.forecastDay[0].day.mintempC.toString(),
        hourlyData = forecast.forecastDay[0].hour.map { hour ->
            HourlyData(
                time = formatHourTime(hour.time),
                temperature = hour.tempC.toString(),
                weatherIcon = hour.condition.icon,
                changeOfRain = hour.chanceOfRain
            )
        }
    )
}
