
package com.dresscode.app.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.WeatherData
import com.dresscode.app.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherRepository = WeatherRepository()

    private val _weather = MutableLiveData<Result<WeatherData>>()
    val weather: LiveData<Result<WeatherData>> = _weather

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weather.value = Result.Loading
            try {
                val response = weatherRepository.getWeather(latitude, longitude)
                if (response.isSuccessful && response.body() != null) {
                    val owmResponse = response.body()!!
                    // Map the complex API response to the simple data class the UI uses
                    val uiData = WeatherData(
                        cityName = owmResponse.name,
                        temperature = owmResponse.main.temp,
                        description = owmResponse.weather.firstOrNull()?.main ?: "Unknown",
                        icon = owmResponse.weather.firstOrNull()?.icon ?: ""
                    )
                    _weather.value = Result.Success(uiData)
                } else {
                    val error = response.errorBody()?.string() ?: "Failed to fetch weather"
                    _weather.value = Result.Error(Exception(error))
                }
            } catch (e: Exception) {
                _weather.value = Result.Error(e)
            }
        }
    }
}
