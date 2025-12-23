
package com.dresscode.app.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.WeatherData
import com.dresscode.app.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    private val weatherRepository = WeatherRepository()

    private val _weather = MutableLiveData<Result<WeatherData>>()
    val weather: LiveData<Result<WeatherData>> = _weather

    fun fetchWeatherByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weather.value = Result.Loading
            try {
                val response = weatherRepository.getWeatherByLocation(latitude, longitude)
                handleWeatherResponse(response)
            } catch (e: Exception) {
                _weather.value = Result.Error(e)
            }
        }
    }

    fun fetchWeatherByCityName(cityName: String) {
        viewModelScope.launch {
            _weather.value = Result.Loading
            try {
                val response = weatherRepository.getWeatherByCityName(cityName)
                handleWeatherResponse(response)
            } catch (e: Exception) {
                _weather.value = Result.Error(e)
            }
        }
    }

    private fun handleWeatherResponse(response: Response<com.dresscode.app.data.model.owm.WeatherResponse>) {
        if (response.isSuccessful && response.body() != null) {
            val owmResponse = response.body()!!
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
    }
}
