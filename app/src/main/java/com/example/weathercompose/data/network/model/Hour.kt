package com.example.weathercompose.data.network.model


import com.google.gson.annotations.SerializedName

data class Hour(
    @SerializedName("chance_of_rain")
    val chanceOfRain: Double,
    @SerializedName("chance_of_snow")
    val chanceOfSnow: Double,
    @SerializedName("cloud")
    val cloud: Double,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("dewpoDouble_c")
    val dewpoDoubleC: Double,
    @SerializedName("dewpoDouble_f")
    val dewpoDoubleF: Double,
    @SerializedName("feelslike_c")
    val feelslikeC: Double,
    @SerializedName("feelslike_f")
    val feelslikeF: Double,
    @SerializedName("gust_kph")
    val gustKph: Double,
    @SerializedName("gust_mph")
    val gustMph: Double,
    @SerializedName("heatindex_c")
    val heatindexC: Double,
    @SerializedName("heatindex_f")
    val heatindexF: Double,
    @SerializedName("humidity")
    val humidity: Double,
    @SerializedName("is_day")
    val isDay: Double,
    @SerializedName("precip_in")
    val precipIn: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("pressure_in")
    val pressureIn: Double,
    @SerializedName("pressure_mb")
    val pressureMb: Double,
    @SerializedName("snow_cm")
    val snowCm: Double,
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("temp_f")
    val tempF: Double,
    @SerializedName("time")
    val time: String,
    @SerializedName("time_epoch")
    val timeEpoch: Double,
    @SerializedName("uv")
    val uv: Double,
    @SerializedName("vis_km")
    val visKm: Double,
    @SerializedName("vis_miles")
    val visMiles: Double,
    @SerializedName("will_it_rain")
    val willItRain: Double,
    @SerializedName("will_it_snow")
    val willItSnow: Double,
    @SerializedName("wind_degree")
    val windDegree: Double,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("wind_mph")
    val windMph: Double,
    @SerializedName("windchill_c")
    val windchillC: Double,
    @SerializedName("windchill_f")
    val windchillF: Double
)