package io.dhruv.weatherwise

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.dhruv.weatherwise.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class WeatherViewModel : ViewModel() {
    private val locationTracker: DefaultLocationTracker
    private val repo = WeatherRepository(context = AppContext.context!!)
    private var _posts: MutableLiveData<ResponseModal> = MutableLiveData()
    val posts: LiveData<ResponseModal> get() = _posts

    val _isGpsAvailable : MutableLiveData<Boolean> = MutableLiveData()
    val isGpsAvailable: LiveData<Boolean> get() = _isGpsAvailable
    init {
        locationTracker = DefaultLocationTracker(
            LocationServices.getFusedLocationProviderClient(AppContext.context!!),
            AppContext.context!!
        )
        getPosts()
    }

    fun getPosts() {

        viewModelScope.launch(Dispatchers.IO) {
            val connectivityObserver = NetworkConnectivityObserver(AppContext.context!!)
            val isNetworkAvailable = connectivityObserver.observeConnectivityAsFlow().first() == ConnectionState.Available

            if (isNetworkAvailable.not()){
                _posts.postValue(repo.getCachedWeather())
            }else{
                locationTracker.getCurrentLocation()?.let { location ->
                    Log.e("TAG", "1: ")
                    launch {
                        Log.e("TAG", "2: ")
                        repo.data.collect {
                            Log.e("TAG", "3: ")
                            _posts.postValue(it)
                            Log.e("TAG", "getPosts: $it")
                        }
                    }
                    repo.getWeather(location.latitude, location.longitude) //gps is on so get weather
                } ?: run {
                    // when gps is off: take permission
                _isGpsAvailable.postValue(false)
//                    repo.getWeather()
                }
            }
        }
    }
}

class DefaultLocationTracker(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            Log.e("TAG", "getCurrentLocation: ")
            return null
        }

        return suspendCancellableCoroutine { cont ->
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).apply {
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result)
                    } else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }
}

