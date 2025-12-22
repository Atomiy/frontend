
package com.dresscode.app.data.repository

import com.dresscode.app.BuildConfig
import com.dresscode.app.data.remote.OwmRetrofitClient
import com.dresscode.app.data.remote.WeatherApiService

class WeatherRepository {

    private val weatherService: WeatherApiService by lazy {
        OwmRetrofitClient.instance.create(WeatherApiService::class.java)
    }

    suspend fun getWeather(latitude: Double, longitude: Double) = 
        weatherService.getWeather(latitude, longitude, BuildConfig.OWM_API_KEY)
}

