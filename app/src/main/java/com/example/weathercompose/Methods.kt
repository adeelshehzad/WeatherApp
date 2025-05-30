package com.example.weathercompose

import android.content.Context
import org.json.JSONObject

fun loadCityListFromAssets(context: Context): List<String> {
    val jsonString =
        context.assets.open("ontario_cities.json").bufferedReader().use { it.readText() }
    val json = JSONObject(jsonString)
    val citiesArray = json.getJSONArray("cities")

    val cityList = mutableListOf<String>()
    for (i in 0 until citiesArray.length()) {
        val cityObj = citiesArray.getJSONObject(i)
        val cityName = cityObj.getString("city")
        val province = cityObj.getString("province")
        cityList.add("$cityName, $province")
    }
    return cityList
}

fun formatHourTime(time: String) : String {
    //format time from yyyy-MM-dd HH:mm format to HH:mm a format
    val hour = time.substring(11, 13).toInt()
    val minute = time.substring(14, 16)
    val amPm = if (hour < 12) "AM" else "PM"
    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
    return "$formattedHour:$minute $amPm"
}