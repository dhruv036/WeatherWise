package io.dhruv.weatherwise.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import io.dhruv.weatherwise.data.model.ResponseModal
import io.dhruv.weatherwise.data.repository.WeatherRepository
import io.dhruv.weatherwise.utils.AppContext
import io.dhruv.weatherwise.utils.ConnectionState
import io.dhruv.weatherwise.utils.location.DefaultLocationTracker
import io.dhruv.weatherwise.utils.location.NetworkConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val locationTracker: DefaultLocationTracker
    private val repo = WeatherRepository(context = AppContext.context!!)
    private var _posts: MutableLiveData<ResponseModal> = MutableLiveData()
    val posts: LiveData<ResponseModal> get() = _posts

    val _isGpsAvailable : MutableLiveData<Boolean> = MutableLiveData()
    val isGpsAvailable: LiveData<Boolean> get() = _isGpsAvailable

    val _isDataReady : MutableLiveData<Boolean> = MutableLiveData()
    val isDataReady: LiveData<Boolean> get() = _isDataReady

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
            _isDataReady.postValue(false)
            if (isNetworkAvailable.not()){
                _posts.postValue(repo.getCachedWeather())
                _isDataReady.postValue(true)
            }else{
                locationTracker.getCurrentLocation()?.let { location ->
                    launch {
                        repo.data.collect {
                            _posts.postValue(it)
                            _isDataReady.postValue(true)
                        }
                    }
                    repo.getWeather(location.latitude, location.longitude) //gps is on so get weather
                } ?: run {
                    // when gps is off: take permission
                    _isDataReady.postValue(true)
                    _isGpsAvailable.postValue(false)
                }
            }
        }
    }
}


