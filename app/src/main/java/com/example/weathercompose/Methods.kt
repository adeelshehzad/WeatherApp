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