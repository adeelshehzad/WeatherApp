package com.example.weathercompose.data.network.model


import com.example.weathercompose.utils.formatHourTime
import com.example.weathercompose.data.model.ForecastData
import com.example.weathercompose.data.model.HourlyData
import com.example.weathercompose.data.model.WeatherData
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
        precipitation = current.precipMm.toString(),
        windSpeed = current.windKph.toString(),
        windDirection = current.windDir,
        windDegree = current.windDegree,
        highTemperature = forecast.forecastDay[0].day.maxtempC.toString(),
        lowTemperature = forecast.forecastDay[0].day.mintempC.toString(),
        sunrise = forecast.forecastDay[0].astro.sunrise,
        sunset = forecast.forecastDay[0].astro.sunset,
        hourlyData = forecast.forecastDay[0].hour.map { hour ->
            HourlyData(
                time = formatHourTime(hour.time),
                temperature = hour.tempC.toString(),
                weatherIcon = hour.condition.icon,
                changeOfRain = hour.chanceOfRain
            )
        },
        threeDayForecast = forecast.forecastDay.map { forecast ->
            ForecastData(
                date = forecast.date,
                highTemperature = forecast.day.maxtempC.toString(),
                lowTemperature = forecast.day.mintempC.toString(),
                weatherIcon = forecast.day.condition.icon,
                chanceOfRain = forecast.day.dailyChanceOfRain
            )
        }
    )
}
