package com.example.weathercompose.data.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.weathercompose.BuildConfig
import com.example.weathercompose.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.InputStream

interface WeatherApiRepository {
    suspend fun getCurrentWeather(postalCode: String): Weather
    suspend fun getWeatherIcon(iconPath: String): Bitmap?
    suspend fun convertResponseBodyToBitmap(responseBody: ResponseBody): Bitmap?
}

class WeatherApiRepositoryImpl(private val apiService: WeatherApiService) : WeatherApiRepository {
    override suspend fun getCurrentWeather(postalCode: String): Weather {
        return try {
            val response = apiService.getCurrentWeather(postalCode, BuildConfig.WEATHER_API_KEY)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API request failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getWeatherIcon(iconPath: String): Bitmap? {
        return try {
            val response = apiService.getWeatherIcon(iconPath)
            val responseBody = if (response.isSuccessful) {
                response.body() ?: throw Exception("Empty response body")
            } else {
                throw Exception("API request failed with code ${response.code()}")
            }
            convertResponseBodyToBitmap(responseBody)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun convertResponseBodyToBitmap(responseBody: ResponseBody): Bitmap? {
        return withContext(Dispatchers.IO) {
            var inputStream: InputStream? = null
            try {
                inputStream = responseBody.byteStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                throw e
            } finally {
                inputStream?.close()
                responseBody.close()
            }
        }
    }
}