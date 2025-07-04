package com.example.weathercompose.data.network.model


import com.google.gson.annotations.SerializedName

data class Astro(
    @SerializedName("is_moon_up")
    val isMoonUp: Double,
    @SerializedName("is_sun_up")
    val isSunUp: Double,
    @SerializedName("moon_illumination")
    val moonIllumination: Double,
    @SerializedName("moon_phase")
    val moonPhase: String,
    @SerializedName("moonrise")
    val moonrise: String,
    @SerializedName("moonset")
    val moonset: String,
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String
)