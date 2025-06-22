package com.example.weathercompose.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.data.local.City
import com.example.weathercompose.data.network.model.toWeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: WeatherApiRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> get() = _uiState.asStateFlow()

    private val _weatherStateFlow = MutableStateFlow<WeatherData?>(null)
    val weatherStateFlow: StateFlow<WeatherData?> get() = _weatherStateFlow.asStateFlow()

    private val _savedCitiesStateFlow = MutableStateFlow<List<SavedCityWeather>>(emptyList())
    val savedCitiesStateFlow: StateFlow<List<SavedCityWeather>> get() = _savedCitiesStateFlow.asStateFlow()

    fun getWeather(location: String) {
        viewModelScope.launch {
            _uiState.emit(UIState.Loading)
            try {
                val weather = repository.getWeather(location, 3)
                _weatherStateFlow.emit(weather.toWeatherData())
                _uiState.emit(UIState.Success)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.emit(UIState.Error(e))
            }
        }
    }

    fun getSavedCities() {
        viewModelScope.launch {
            _uiState.emit(UIState.Loading)
            try {
                val savedCitiesWeather = withContext(Dispatchers.IO) {
                    val cities = repository.getSavedCities()
                    cities.map { city ->
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
                _uiState.emit(UIState.Success)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.emit(UIState.Error(e))
            }
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