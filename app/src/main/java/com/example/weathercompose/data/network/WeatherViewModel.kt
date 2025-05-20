package com.example.weathercompose.data.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherApiRepository) : ViewModel() {
    private val _loadingLiveData = MutableStateFlow(false)
    val loadingLiveData: StateFlow<Boolean> get() = _loadingLiveData

    private val _errorLiveData = MutableStateFlow<String?>(null)
    val errorLiveData: StateFlow<String?> get() = _errorLiveData

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> get() = _currentWeather

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