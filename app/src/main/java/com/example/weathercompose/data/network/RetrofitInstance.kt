package com.example.weathercompose.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    const val BASE_URL = "https://api.weatherapi.com/v1/"

    private fun getRetrofitInstance(context: Context): Retrofit {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val cache = Cache(File(context.cacheDir, "http-cache"), cacheSize)

        val cacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(30, TimeUnit.MINUTES)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        }

        val offlineInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!hasNetwork(context)) {
                val cacheControl = CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(1, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }

        val cacheHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor) // Add this line for logging
            .addInterceptor(cacheInterceptor)
            .addInterceptor(offlineInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cacheHttpClient) // Set the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // For JSON parsing
            .build()
    }

    private fun hasNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    fun getWeatherApiService(context: Context): WeatherApiService {
        return getRetrofitInstance(context).create(WeatherApiService::class.java)
    }
}