package io.dhruv.weatherwise.repository

import android.content.Context
import android.util.Log
import androidx.datastore.dataStore
import io.dhruv.weatherwise.ConnectionState
import io.dhruv.weatherwise.NetworkConnectivityObserver
import io.dhruv.weatherwise.ResponseModal
import io.dhruv.weatherwise.ResponseModalSerializer
import io.dhruv.weatherwise.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


val Context.dataStore by dataStore(fileName = "weather_data.json", ResponseModalSerializer)


class WeatherRepository(val context: Context) {
    private var _data = MutableStateFlow<ResponseModal>(ResponseModal())
    val data: StateFlow<ResponseModal> get() = _data

    suspend fun getWeather(lat: Double = 0.0, lon: Double = 0.0) {
        val connectivityObserver = NetworkConnectivityObserver(context)
        val apiService = RetrofitInstance.api
        connectivityObserver.observeConnectivityAsFlow()
            .collect { it ->
                when (it) {
                    ConnectionState.Available -> {
                        Log.e("TAG", "getWeather: ")
                        apiService.getPosts(lat, lon).apply {
                            Log.e("TAG", "Thread: ${Thread.currentThread().name}", )
                               _data.value = this
                            Log.e("TAG", "data: $this")
                        }
                        withContext(Dispatchers.Main){
                            storeWeatherLocally(_data.value!!)

                        }
                        return@collect
                    }

                    ConnectionState.Unavailable -> {
                        Log.e("TAG", "getWeather2: ")
                      context.dataStore.data.collect {
                          Log.e("TAG", "data2: $it")
                            _data.value = it
                            Log.e("TAG", "data2 from livedata: ${_data.value}")
                      }
                    }
                }
            }
        Log.e("TAG", "data from livedata: ${_data.value}")


    }


    suspend fun getCachedWeather() :ResponseModal{
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

