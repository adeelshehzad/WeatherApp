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
    private val _loadingLiveData = MutableStateFlow(false)
    val loadingLiveData: StateFlow<Boolean> get() = _loadingLiveData

    private val _errorLiveData = MutableStateFlow<String?>(null)
    val errorLiveData: StateFlow<String?> get() = _errorLiveData

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> get() = _currentWeather

    private val _savedCities = MutableStateFlow<List<SavedCityWeather>>(emptyList())
    val savedCities: StateFlow<List<SavedCityWeather>> get() = _savedCities

    fun getCurrentWeather(postalCode: String) {
        viewModelScope.launch {
            _loadingLiveData.emit(true)
            try {
                val weather = repository.getCurrentWeather(postalCode)

                val weatherData = WeatherData(
                    currentTemperature = weather.current?.temp_c.toString(),
                    locationName = weather.location?.name.orEmpty(),
                    weatherCondition = weather.current?.condition?.text.orEmpty(),
                    weatherIcon = weather.current?.condition?.icon.orEmpty()
                )
                _currentWeather.emit(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorLiveData.emit(e.message)
            }
            _loadingLiveData.emit(false)
        }
    }

    fun getSavedCities() {
        viewModelScope.launch {
            _loadingLiveData.emit(true)
            try {
                val savedCitiesWeather = withContext(Dispatchers.IO) {
                    val cities = repository.getSavedCities()
                    cities.map { city ->
                        Log.d(WeatherViewModel::class.java.simpleName, "Fetching weather for $city")
                        val weather = repository.getCurrentWeather(city.cityName)
                        SavedCityWeather(
                            cityName = city.cityName,
                            currentTemperature = weather.current?.temp_c.toString(),
                            weatherCondition = weather.current?.condition?.text.orEmpty(),
                            weatherIcon = weather.current?.condition?.icon.orEmpty()
                        )
                    }
                }
                _savedCities.emit(savedCitiesWeather)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorLiveData.emit(e.message)
            }
            _loadingLiveData.emit(false)
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