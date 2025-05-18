package com.example.weathercompose.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.weathercompose.model.Weather
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherApiRepository) : ViewModel() {
    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> get() = _loadingLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    private val _currentWeather = MutableLiveData<Weather>()
    val currentWeather: LiveData<Weather> get() = _currentWeather

    fun getCurrentWeather(postalCode: String) {
        viewModelScope.launch {
            _loadingLiveData.postValue(true)
            try {
                val weather = repository.getCurrentWeather(postalCode)
                _currentWeather.postValue(weather)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorLiveData.postValue(e.message)
            }
            _loadingLiveData.postValue(false)
        }
    }
}

class WeatherViewModelFactory(private val repository: WeatherApiRepository): ViewModelProvider.Factory {
    fun create(modelClass: Class<WeatherViewModel>): WeatherViewModel {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository)
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}