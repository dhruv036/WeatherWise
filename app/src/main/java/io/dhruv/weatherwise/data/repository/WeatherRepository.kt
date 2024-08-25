package io.dhruv.weatherwise.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import io.dhruv.weatherwise.BuildConfig
import io.dhruv.weatherwise.data.model.ResponseModal
import io.dhruv.weatherwise.utils.ResponseModalSerializer
import io.dhruv.weatherwise.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class WeatherRepository(val context: Context) {
    val Context.dataStore by dataStore(fileName = "weather_data.json", ResponseModalSerializer)
    private var _data = MutableStateFlow<ResponseModal>(ResponseModal())
    val data: StateFlow<ResponseModal> get() = _data

    suspend fun getWeather(lat: Double = 0.0, lon: Double = 0.0) {
        val apiService = RetrofitInstance.api
        apiService.getPosts(lat, lon, BuildConfig.MY_API_KEY).apply {
            _data.value = this
        }
        withContext(Dispatchers.Main) {
            storeWeatherLocally(_data.value!!)
        }
    }


    suspend fun getCachedWeather(): ResponseModal {
        Log.e("TAG", "getWeather2: ")
        return context.dataStore.data.first()
    }

    private suspend fun storeWeatherLocally(response: ResponseModal) {
        context.dataStore.updateData {
            it.copy(
                coord = response.coord,
                weather = response.weather,
                base = response.base,
                main = response.main,
                visibility = response.visibility,
                wind = response.wind,
                clouds = response.clouds,
                dt = response.dt,
                sys = response.sys,
                timezone = response.timezone,
                id = response.id,
                name = response.name,
                cod = response.cod
            )
        }
    }
}

