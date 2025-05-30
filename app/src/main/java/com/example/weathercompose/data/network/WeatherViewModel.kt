package com.example.weathercompose.data.network

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.local.City
import com.example.weathercompose.model.SavedCityWeather
import com.example.weathercompose.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: WeatherApiRepository) : ViewModel() {
    private val _loadingStateFlow = MutableStateFlow(false)
    val loadingStateFlow: StateFlow<Boolean> get() = _loadingStateFlow

    private val _errorStateFlow = MutableStateFlow<String?>(null)
    val errorStateFlow: StateFlow<String?> get() = _errorStateFlow

    private val _weatherStateFlow = MutableStateFlow<WeatherData?>(null)
    val weatherStateFlow: StateFlow<WeatherData?> get() = _weatherStateFlow

    private val _savedCitiesStateFlow = MutableStateFlow<List<SavedCityWeather>>(emptyList())
    val savedCitiesStateFlow: StateFlow<List<SavedCityWeather>> get() = _savedCitiesStateFlow

    fun getWeather(location: String) {
        viewModelScope.launch {
            _loadingStateFlow.emit(true)
            try {
                val weather = repository.getWeather(location, 3)

                val weatherData = WeatherData(
                    currentTemperature = weather.current.tempC.toString(),
                    weatherCondition = weather.current.condition.text,
                    locationName = weather.location.name,
                    weatherIcon = weather.current.condition.icon,
                    feelsLike = weather.current.feelslikeC.toString(),
                    highTemperature = weather.forecast.forecastDay[0].day.maxtempC.toString(),
                    lowTemperature = weather.forecast.forecastDay[0].day.mintempC.toString()
                )
                _weatherStateFlow.emit(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorStateFlow.emit(e.message)
            }
            _loadingStateFlow.emit(false)
        }
    }

    fun getSavedCities() {
        viewModelScope.launch {
            _loadingStateFlow.emit(true)
            try {
                val savedCitiesWeather = withContext(Dispatchers.IO) {
                    val cities = repository.getSavedCities()
                    cities.map { city ->
                        Log.d(WeatherViewModel::class.java.simpleName, "Fetching weather for $city")
                        val weather = repository.getWeather(city.cityName, 1)
                        SavedCityWeather(
                            cityName = city.cityName,
                            currentTemperature = weather.current.tempC.toString(),
                            weatherCondition = weather.current.condition.text,
                            weatherIcon = weather.current.condition.icon
                        )
                    }
                }
                _savedCitiesStateFlow.emit(savedCitiesWeather)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorStateFlow.emit(e.message)
            }
            _loadingStateFlow.emit(false)
        }
    }

    fun saveCity(cityName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.saveCity(City(cityName = cityName))
                getSavedCities()
            }
        }
    }
}

class WeatherViewModelFactory(private val repository: WeatherApiRepository) :
    ViewModelProvider.Factory {
    fun create(modelClass: Class<WeatherViewModel>): WeatherViewModel {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository)
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}